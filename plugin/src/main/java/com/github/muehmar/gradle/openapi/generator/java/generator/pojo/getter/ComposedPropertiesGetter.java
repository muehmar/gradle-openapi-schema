package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ONE_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class ComposedPropertiesGetter {
  private ComposedPropertiesGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> composedPropertiesGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(notNullAnnotation())
        .append(method())
        .append(fieldRefs())
        .filter(ComposedPropertiesGetter::composedPropertiesGetterFilter);
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullAnnotation() {
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIncludeNonNull()
        .filter(JavaPojoMember::isOptional);
  }

  private static boolean composedPropertiesGetterFilter(
      JavaPojoMember member, PojoSettings settings) {
    return (member.getType().equals(ONE_OF_MEMBER) || member.getType().equals(ANY_OF_MEMBER))
        && settings.isJacksonJson();
  }

  private static Generator<JavaPojoMember, PojoSettings> method() {
    return MethodGenBuilder.<JavaPojoMember, PojoSettings>create()
        .modifiers(JavaModifier.PRIVATE)
        .noGenericTypes()
        .returnType(ComposedPropertiesGetter::returnType)
        .methodName(m -> m.getGetterName().asString())
        .noArguments()
        .content(methodContent())
        .build();
  }

  private static String returnType(JavaPojoMember member) {
    return member.isOptionalAndNullable()
        ? "Object"
        : member.getJavaType().getFullClassName().asString();
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return normalGetterMethodContent().append(getOptionalNullableGetter());
  }

  private static Generator<JavaPojoMember, PojoSettings> getOptionalNullableGetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    m.getIsNullFlagName(), m.getNameAsIdentifier(), m.getNameAsIdentifier()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> normalGetterMethodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("return %s;", m.getNameAsIdentifier()))
        .filter(m -> not(m.isOptionalAndNullable()));
  }
}
