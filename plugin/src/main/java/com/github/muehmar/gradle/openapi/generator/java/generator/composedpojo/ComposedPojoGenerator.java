package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ConversionMethodGenerator.asDtoMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ValidationMethodGenerator.isValidAgainstMethod;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.FieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class ComposedPojoGenerator implements Generator<JavaComposedPojo, PojoSettings> {

  private final Generator<JavaComposedPojo, PojoSettings> delegate;

  public ComposedPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .enum_()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc()
                    .contraMap(JavaComposedPojo::getDescription))
            .singleAnnotation(JacksonAnnotationGenerator.jsonDeserialize())
            .modifiers(PUBLIC)
            .className(enumPojo -> enumPojo.getName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaComposedPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaComposedPojo, PojoSettings> content() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(FieldsGenerator.fields(), JavaComposedPojo::getJavaPojos)
        .appendNewLine()
        .append(PojoConstructorGenerator.generator(), JavaComposedPojo::wrapIntoJavaObjectPojo)
        .appendNewLine()
        .append(new NormalBuilderGenerator(), JavaComposedPojo::wrapIntoJavaObjectPojo)
        .appendList(memberGetter().prependNewLine(), JavaComposedPojo::getMembers)
        .appendNewLine()
        .append(FactoryMethodGenerator.generator())
        .appendNewLine()
        .append(ValidCountMethodGenerator.validCountMethod())
        .appendList(isValidAgainstMethod().prependNewLine(), JavaComposedPojo::getJavaPojos)
        .appendList(asDtoMethod().prependNewLine(), JavaComposedPojo::getJavaPojos)
        .appendNewLine()
        .append(HashCodeGenerator.hashCodeMethod(), JavaComposedPojo::wrapIntoJavaObjectPojo)
        .appendNewLine()
        .append(EqualsGenerator.equalsMethod(), JavaComposedPojo::wrapIntoJavaObjectPojo)
        .appendNewLine()
        .append(ToStringGenerator.toStringMethod(), JavaComposedPojo::wrapIntoJavaObjectPojo);
  }

  private Generator<JavaPojoMember, PojoSettings> memberGetter() {
    final MethodGen<JavaPojoMember, PojoSettings> method =
        MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
            .modifiers(PRIVATE)
            .noGenericTypes()
            .returnType(member -> member.getJavaType().getFullClassName().asString())
            .methodName(member -> member.getGetterName().asString())
            .noArguments()
            .content(memberGetterContent())
            .build();
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JacksonAnnotationGenerator.jsonProperty())
        .append(JacksonAnnotationGenerator.jsonIncludeNonNull())
        .append(method)
        .filter((member, settings) -> settings.isJacksonJson());
  }

  private Generator<JavaPojoMember, PojoSettings> memberGetterContent() {
    final Generator<JavaPojoMember, PojoSettings> requiredOrOptionalMember =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append((member, settings, writer) -> writer.println("return %s;", member.getName()))
            .filter(
                member -> member.isRequiredAndNotNullable() || member.isOptionalAndNotNullable());

    final Generator<JavaPojoMember, PojoSettings> requiredNullableMember =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(
                (member, settings, writer) ->
                    writer.println(
                        "return %s ? new JacksonNullContainer<>(%s) : null;",
                        member.getIsPresentFlagName(), member.getName()))
            .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
            .filter(JavaPojoMember::isRequiredAndNullable);

    final Generator<JavaPojoMember, PojoSettings> optionalNullableMember =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(
                (member, settings, writer) ->
                    writer.println(
                        "return %s ? new JacksonNullContainer<>(%s) : %s;",
                        member.getIsNullFlagName(), member.getName(), member.getName()))
            .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
            .filter(JavaPojoMember::isOptionalAndNullable);

    return requiredOrOptionalMember.append(requiredNullableMember).append(optionalNullableMember);
  }
}
