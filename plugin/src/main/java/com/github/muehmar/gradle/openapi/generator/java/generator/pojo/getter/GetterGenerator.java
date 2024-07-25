package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedPropertiesGetter.composedPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ONE_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> getterGenerator() {
    return membersGenerator().append(allOfGenerator()).append(oneOfAndAnyOfGenerator());
  }

  private static Generator<JavaPojoMember, PojoSettings> membersGenerator() {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(m -> m.getType().equals(OBJECT_MEMBER) || m.getType().equals(ARRAY_VALUE));
  }

  private static Generator<JavaPojoMember, PojoSettings> allOfGenerator() {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(m -> m.getType().equals(ALL_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> oneOfAndAnyOfGenerator() {
    return composedPropertiesGetterGenerator()
        .filter(m -> m.getType().equals(ONE_OF_MEMBER) || m.getType().equals(ANY_OF_MEMBER));
  }

  private static boolean isNullableListItemsMember(JavaPojoMember member) {
    return member.getJavaType().isNullableItemsArrayType();
  }

  private static boolean isNotNullableListItemsMember(JavaPojoMember member) {
    return not(isNullableListItemsMember(member));
  }
}
