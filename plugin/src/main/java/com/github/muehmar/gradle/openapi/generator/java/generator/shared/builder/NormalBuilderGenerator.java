package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGen;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.ConstructorGen;
import io.github.muehmar.codegenerator.java.ConstructorGenBuilder;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiFunction;

public class NormalBuilderGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private static final String RETURN_THIS = "return this;";

  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public NormalBuilderGenerator() {
    final ClassGen<JavaObjectPojo, PojoSettings> classGen =
        ClassGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .clazz()
            .nested()
            .packageGen(new PackageGenerator<>())
            .noJavaDoc()
            .noAnnotations()
            .modifiers(PUBLIC, STATIC, FINAL)
            .className("Builder")
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
    this.delegate =
        this.<JavaObjectPojo>factoryMethod()
            .append(JacksonAnnotationGenerator.jsonPojoBuilderWithPrefix("set"))
            .append(classGen);
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private <A> Generator<A, PojoSettings> factoryMethod() {
    return Generator.<A, PojoSettings>constant("public static Builder newBuilder() {")
        .append(Generator.constant("return new Builder();"), 1)
        .append(Generator.constant("}"))
        .appendNewLine()
        .filter((data, settings) -> settings.isDisableSafeBuilder());
  }

  private Generator<JavaObjectPojo, PojoSettings> content() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendNewLine()
        .append(constructor())
        .appendList(memberDeclaration(), JavaObjectPojo::getMembers)
        .append(additionalPropertiesDeclaration())
        .appendNewLine()
        .appendList(setter(), JavaObjectPojo::getMembers)
        .appendSingleBlankLine()
        .append(additionalPropertiesSetters())
        .appendSingleBlankLine()
        .append(buildMethod());
  }

  private <A> Generator<A, PojoSettings> constructor() {
    final ConstructorGen<A, PojoSettings> constructor =
        ConstructorGenBuilder.<A, PojoSettings>create()
            .modifiers(PRIVATE)
            .className("Builder")
            .noArguments()
            .noContent()
            .build();
    return constructor.appendNewLine().filter((data, settings) -> settings.isEnableSafeBuilder());
  }

  private <B> Generator<JavaPojoMember, B> memberDeclaration() {
    return this.<B>normalMemberDeclaration()
        .append(memberIsPresentFlagDeclaration())
        .append(memberIsNullFlagDeclaratino());
  }

  private <B> Generator<JavaPojoMember, B> normalMemberDeclaration() {
    return ((member, settings, writer) ->
        writer.println(
            "private %s %s;",
            member.getJavaType().getFullClassName(), member.getNameAsIdentifier()));
  }

  private <B> Generator<JavaPojoMember, B> memberIsPresentFlagDeclaration() {
    final Generator<JavaPojoMember, B> generator =
        (member, settings, writer) ->
            writer.println("private boolean %s = false;", member.getIsPresentFlagName());
    return generator.filter(JavaPojoMember::isRequiredAndNullable);
  }

  private <B> Generator<JavaPojoMember, B> memberIsNullFlagDeclaratino() {
    final Generator<JavaPojoMember, B> generator =
        (member, settings, writer) ->
            writer.println("private boolean %s = false;", member.getIsNullFlagName());
    return generator.filter(JavaPojoMember::isOptionalAndNullable);
  }

  private <B> Generator<JavaObjectPojo, B> additionalPropertiesDeclaration() {
    final Generator<JavaAdditionalProperties, B> generator =
        (props, settings, writer) ->
            writer.println(
                "private Map<String, %s> %s = new HashMap<>();",
                props.getType().getFullClassName(), props.getPropertyName());
    return generator
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .contraMap(JavaObjectPojo::getAdditionalProperties);
  }

  private Generator<JavaPojoMember, PojoSettings> setter() {
    return standardSetter()
        .appendNewLine()
        .append(requiredNullableSetter())
        .append(optionalSetter())
        .append(optionalNullableSetter());
  }

  private Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetters() {
    return singleAdditionalPropertiesSetter()
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter());
  }

  private Generator<JavaObjectPojo, PojoSettings> singleAdditionalPropertiesSetter() {
    final Generator<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                props ->
                    PList.of(
                        "String key",
                        String.format("%s value", props.getType().getFullClassName())))
            .content(
                (props, s, w) ->
                    w.println("this.%s.put(key, value);", props.getPropertyName())
                        .println(RETURN_THIS))
            .build()
            .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
            .contraMap(JavaObjectPojo::getAdditionalProperties);
    return JacksonAnnotationGenerator.<JavaObjectPojo>jsonAnySetter().append(method);
  }

  private Generator<JavaObjectPojo, PojoSettings> allAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("setAdditionalProperties")
        .singleArgument(
            props ->
                String.format(
                    "Map<String, %s> %s",
                    props.getType().getFullClassName(), props.getPropertyName()))
        .content(
            (props, s, w) ->
                w.println(
                        "this.%s = new HashMap<>(%s);",
                        props.getPropertyName(), props.getPropertyName())
                    .println(RETURN_THIS))
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .contraMap(JavaObjectPojo::getAdditionalProperties);
  }

  private Generator<JavaPojoMember, PojoSettings> standardSetter() {
    final BiFunction<JavaPojoMember, PojoSettings, JavaModifiers> modifiers =
        (member, settings) ->
            settings.isEnableSafeBuilder() && member.isRequired()
                ? JavaModifiers.of(PRIVATE)
                : JavaModifiers.of(PUBLIC);
    final MethodGen<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(modifiers)
            .noGenericTypes()
            .returnType("Builder")
            .methodName(
                (member, settings) ->
                    member.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
            .singleArgument(
                member ->
                    String.format(
                        "%s %s",
                        member.getJavaType().getFullClassName(), member.getNameAsIdentifier()))
            .content(setterMethodContent())
            .build();
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JavaDocGenerator.javaDoc(), JavaPojoMember::getDescription)
        .append(JacksonAnnotationGenerator.jsonProperty())
        .append(method);
  }

  private static Generator<JavaPojoMember, PojoSettings> setterMethodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (member, settings, writer) ->
                writer.println(
                    "this.%s = %s;", member.getNameAsIdentifier(), member.getNameAsIdentifier()))
        .appendConditionally(
            JavaPojoMember::isRequiredAndNullable,
            (member, settings, writer) ->
                writer.println("this.%s = true;", member.getIsPresentFlagName()))
        .appendConditionally(
            JavaPojoMember::isOptionalAndNullable,
            (member, settings, writer) ->
                writer.println(
                    "this.%s = %s == null;",
                    member.getIsNullFlagName(), member.getNameAsIdentifier()))
        .append(w -> w.println(RETURN_THIS));
  }

  private Generator<JavaPojoMember, PojoSettings> requiredNullableSetter() {
    final BiFunction<JavaPojoMember, PojoSettings, JavaModifiers> modifiers =
        (member, settings) ->
            settings.isEnableSafeBuilder() ? JavaModifiers.of(PRIVATE) : JavaModifiers.of(PUBLIC);
    final Generator<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(modifiers)
            .noGenericTypes()
            .returnType("Builder")
            .methodName(
                (member, settings) ->
                    member.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
            .singleArgument(
                member ->
                    String.format(
                        "Optional<%s> %s",
                        member.getJavaType().getFullClassName(), member.getNameAsIdentifier()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println(
                            "this.%s = %s.orElse(null);",
                            member.getNameAsIdentifier(), member.getNameAsIdentifier())
                        .println("this.%s = true;", member.getIsPresentFlagName())
                        .println("return this;")
                        .ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JavaDocGenerator.javaDoc(), JavaPojoMember::getDescription)
        .append(method)
        .appendNewLine()
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private Generator<JavaPojoMember, PojoSettings> optionalSetter() {
    final MethodGen<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("Builder")
            .methodName(
                (member, settings) ->
                    member.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
            .singleArgument(
                member ->
                    String.format(
                        "Optional<%s> %s",
                        member.getJavaType().getFullClassName(), member.getNameAsIdentifier()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println(
                            "this.%s = %s.orElse(null);",
                            member.getNameAsIdentifier(), member.getNameAsIdentifier())
                        .println("return this;")
                        .ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JavaDocGenerator.javaDoc(), JavaPojoMember::getDescription)
        .append(method)
        .appendNewLine()
        .filter(JavaPojoMember::isOptionalAndNotNullable);
  }

  private Generator<JavaPojoMember, PojoSettings> optionalNullableSetter() {
    final MethodGen<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("Builder")
            .methodName(
                (member, settings) ->
                    member.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
            .singleArgument(
                member ->
                    String.format(
                        "Tristate<%s> %s",
                        member.getJavaType().getFullClassName(), member.getNameAsIdentifier()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println(
                            "this.%s = %s.%s;",
                            member.getNameAsIdentifier(),
                            member.getNameAsIdentifier(),
                            member.tristateToProperty())
                        .println(
                            "this.%s = %s.%s;",
                            member.getIsNullFlagName(),
                            member.getNameAsIdentifier(),
                            member.tristateToIsNullFlag())
                        .println("return this;")
                        .ref(OpenApiUtilRefs.TRISTATE))
            .build();

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JavaDocGenerator.javaDoc(), JavaPojoMember::getDescription)
        .append(method)
        .appendNewLine()
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  private Generator<JavaObjectPojo, PojoSettings> buildMethod() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnTypeName(JavaObjectPojo::getClassName)
        .methodName("build")
        .noArguments()
        .content(buildMethodContent())
        .build();
  }

  private Generator<JavaObjectPojo, PojoSettings> buildMethodContent() {
    return (pojo, settings, writer) ->
        writer.print(
            "return new %s(%s);",
            pojo.getClassName(),
            pojo.getMembers().flatMap(JavaPojoMember::createFieldNames).mkString(", "));
  }
}
