package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedPropertiesGetter.composedPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType.STANDARD_NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ONE_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.NullableItemsListGetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> getterGenerator() {
    return membersGenerator().append(allOfGenerator()).append(oneOfAndAnyOfGenerator());
  }

  private static Generator<JavaPojoMember, PojoSettings> membersGenerator() {
    return singleGetterGenerator(STANDARD)
        .append(nullableItemsListGetterGenerator(STANDARD))
        .filter(m -> m.getType().equals(OBJECT_MEMBER) || m.getType().equals(ARRAY_VALUE));
  }

  private static Generator<JavaPojoMember, PojoSettings> allOfGenerator() {
    return singleGetterGenerator(STANDARD_NO_VALIDATION)
        .append(nullableItemsListGetterGenerator(STANDARD_NO_VALIDATION))
        .filter(m -> m.getType().equals(ALL_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> oneOfAndAnyOfGenerator() {
    return composedPropertiesGetterGenerator()
        .filter(m -> m.getType().equals(ONE_OF_MEMBER) || m.getType().equals(ANY_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> singleGetterGenerator(
      GetterType getterType) {
    return RequiredNotNullableGetter.requiredNotNullableGetterGenerator(getterType)
        .append(RequiredNullableGetter.requiredNullableGetterGenerator(getterType))
        .append(OptionalNotNullableGetter.optionalNotNullableGetterGenerator(getterType))
        .append(OptionalNullableGetter.optionalNullableGetterGenerator(getterType))
        .filter(GetterGenerator::isNotNullableListItemsMember);
  }

  private static Generator<JavaPojoMember, PojoSettings> nullableItemsListGetterGenerator(
      GetterType getterType) {
    return NullableItemsListGetterGenerator.nullableItemsListGetterGenerator(getterType)
        .filter(GetterGenerator::isNullableListItemsMember);
  }

  private static boolean isNullableListItemsMember(JavaPojoMember member) {
    return member.getJavaType().isNullableItemsArrayType();
  }

  private static boolean isNotNullableListItemsMember(JavaPojoMember member) {
    return not(isNullableListItemsMember(member));
  }
}
