package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen;
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
}
