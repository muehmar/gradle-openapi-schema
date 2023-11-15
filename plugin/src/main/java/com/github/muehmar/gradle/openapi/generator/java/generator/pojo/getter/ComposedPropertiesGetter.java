package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ONE_OF_MEMBER;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

/**
 * Generates getters for composed properties of anyOf and oneOf compositions:
 *
 * <ul>
 *   <li>A package private method for json serialisation
 *   <li>A package private method for internal usage, i.e. accessible from other DTO's
 * </ul>
 */
class ComposedPropertiesGetter {
  private ComposedPropertiesGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> composedPropertiesGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(jsonMethod())
        .appendSingleBlankLine()
        .append(internalGetter())
        .append(fieldRefs())
        .filter(ComposedPropertiesGetter::composedPropertiesGetterFilter);
  }

  private static boolean composedPropertiesGetterFilter(
      JavaPojoMember member, PojoSettings settings) {
    return (member.getType().equals(ONE_OF_MEMBER) || member.getType().equals(ANY_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> jsonMethod() {
    return MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
        .modifiers()
        .noGenericTypes()
        .returnType(ComposedPropertiesGetter::jsonMethodReturnType)
        .methodName(m -> m.getGetterName().append("Json").asString())
        .noArguments()
        .content(jsonMethodContent())
        .build();
  }

  private static String jsonMethodReturnType(JavaPojoMember member) {
    return member.isNullable()
        ? "Object"
        : member.getJavaType().getParameterizedClassName().asString();
  }

  private static Generator<JavaPojoMember, PojoSettings> jsonMethodContent() {
    return notNullableGetterContent()
        .append(requiredNullableGetterContent())
        .append(optionalNullableGetterContent());
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("return %s;", m.getName()))
        .filter(JavaPojoMember::isNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : null;",
                    m.getIsPresentFlagName(), m.getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> optionalNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    m.getIsNullFlagName(), m.getName(), m.getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> internalGetter() {
    return internalStandardGetter()
        .append(internalOptionalGetter())
        .append(internalTristateGetter());
  }

  private static Generator<JavaPojoMember, PojoSettings> internalStandardGetter() {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore()
        .append(CommonGetter.getterMethod())
        .filter(JavaPojoMember::isRequiredAndNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> internalOptionalGetter() {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore()
        .append(CommonGetter.wrapNullableInOptionalGetterMethod())
        .filter(m -> m.isOptionalAndNotNullable() || m.isRequiredAndNullable());
  }

  private static Generator<JavaPojoMember, PojoSettings> internalTristateGetter() {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore()
        .append(CommonGetter.tristateGetterMethod())
        .filter(JavaPojoMember::isOptionalAndNullable);
  }
}
