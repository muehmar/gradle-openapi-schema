package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ONE_OF_MEMBER;

import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class ComposedPropertiesGetter {
  private ComposedPropertiesGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> composedPropertiesGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(method())
        .append(fieldRefs())
        .filter(ComposedPropertiesGetter::composedPropertiesGetterFilter);
  }

  private static boolean composedPropertiesGetterFilter(
      JavaPojoMember member, PojoSettings settings) {
    return (member.getType().equals(ONE_OF_MEMBER) || member.getType().equals(ANY_OF_MEMBER))
        && settings.isJacksonJson();
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
        .modifiers()
        .noGenericTypes()
        .returnType(ComposedPropertiesGetter::returnType)
        .methodName(m -> m.getGetterName().asString())
        .noArguments()
        .content(methodContent())
        .build();
  }

  private static String returnType(JavaPojoMember member) {
    return member.isNullable() ? "Object" : member.getJavaType().getFullClassName().asString();
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return notNullableGetterContent()
        .append(requiredNullableGetterContent())
        .append(optionalNullableGetterContent());
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("return %s;", m.getNameAsIdentifier()))
        .filter(JavaPojoMember::isNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : null;",
                    m.getIsPresentFlagName(), m.getNameAsIdentifier()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> optionalNullableGetterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    m.getIsNullFlagName(), m.getNameAsIdentifier(), m.getNameAsIdentifier()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }
}
