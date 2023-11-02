package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.BiFunction;

class SetterGenerator {
  private SetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> setterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(setter(), JavaObjectPojo::getAllMembers);
  }

  private static Generator<JavaPojoMember, PojoSettings> setter() {
    return standardSetter()
        .appendNewLine()
        .append(requiredNullableSetter())
        .append(optionalSetter())
        .append(optionalNullableSetter());
  }

  private static Generator<JavaPojoMember, PojoSettings> standardSetter() {
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
                    argument(member.getJavaType().getParameterizedClassName(), member.getName()))
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
                writer.println("this.%s = %s;", member.getName(), member.getName()))
        .appendConditionally(
            (member, settings, writer) ->
                writer.println("this.%s = true;", member.getIsPresentFlagName()),
            JavaPojoMember::isRequiredAndNullable)
        .appendConditionally(
            (member, settings, writer) ->
                writer.println(
                    "this.%s = %s == null;", member.getIsNullFlagName(), member.getName()),
            JavaPojoMember::isOptionalAndNullable)
        .append(w -> w.println("return this;"));
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredNullableSetter() {
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
                    argument(
                        String.format(
                            "Optional<%s>", member.getJavaType().getParameterizedClassName()),
                        member.getName()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println("this.%s = %s.orElse(null);", member.getName(), member.getName())
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

  private static Generator<JavaPojoMember, PojoSettings> optionalSetter() {
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
                    Argument.argument(
                        String.format(
                            "Optional<%s>", member.getJavaType().getParameterizedClassName()),
                        member.getName()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println("this.%s = %s.orElse(null);", member.getName(), member.getName())
                        .println("return this;")
                        .ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();

    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(JavaDocGenerator.javaDoc(), JavaPojoMember::getDescription)
        .append(method)
        .appendNewLine()
        .filter(JavaPojoMember::isOptionalAndNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> optionalNullableSetter() {
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
                    new Argument(
                        String.format(
                            "Tristate<%s>", member.getJavaType().getParameterizedClassName()),
                        member.getName().asString()))
            .content(
                (member, settings, writer) ->
                    writer
                        .println(
                            "this.%s = %s.%s;",
                            member.getName(), member.getName(), member.tristateToProperty())
                        .println(
                            "this.%s = %s.%s;",
                            member.getIsNullFlagName(),
                            member.getName(),
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
}
