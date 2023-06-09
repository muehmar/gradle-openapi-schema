package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ONE_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> generator() {
    return RequiredNotNullableGetter.getter()
        .append(RequiredNullableGetter.getter())
        .append(OptionalNotNullableGetter.getter())
        .append(OptionalNullableGetter.getter())
        .filter(GetterGenerator::isNotAnyOfOrOneOfMember)
        .append(ComposedPropertiesGetter.generator());
  }

  private static boolean isAnyOfOrOneOfMember(JavaPojoMember member) {
    return member.getType().equals(ONE_OF_MEMBER) || member.getType().equals(ANY_OF_MEMBER);
  }

  private static boolean isNotAnyOfOrOneOfMember(JavaPojoMember member) {
    return not(isAnyOfOrOneOfMember(member));
  }
}
