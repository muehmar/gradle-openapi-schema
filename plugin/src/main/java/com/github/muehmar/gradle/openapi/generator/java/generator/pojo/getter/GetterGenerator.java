package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedPropertiesGetter.composedPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
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
import java.util.function.Predicate;

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
    return singleGetterGenerator(NO_VALIDATION)
        .append(nullableItemsListGetterGenerator(NO_VALIDATION))
        .filter(m -> m.getType().equals(ALL_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> oneOfAndAnyOfGenerator() {
    return composedPropertiesGetterGenerator()
        .filter(m -> m.getType().equals(ONE_OF_MEMBER) || m.getType().equals(ANY_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> singleGetterGenerator(
      GeneratorOption option) {
    return RequiredNotNullableGetter.requiredNotNullableGetterGenerator(option)
        .append(RequiredNullableGetter.requiredNullableGetterGenerator(option))
        .append(OptionalNotNullableGetter.optionalNotNullableGetterGenerator(option))
        .append(OptionalNullableGetter.optionalNullableGetterGenerator(option))
        .filter(GetterGenerator::isNotNullableListItemsMember);
  }

  private static Generator<JavaPojoMember, PojoSettings> nullableItemsListGetterGenerator(
      GeneratorOption option) {
    return NullableItemsListGetterGenerator.nullableItemsListGetterGenerator(option)
        .filter(GetterGenerator::isNullableListItemsMember);
  }

  private static boolean isNullableListItemsMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(arrayType -> arrayType.getItemType().getNullability().isNullable())
        .orElse(false);
  }

  private static boolean isNotNullableListItemsMember(JavaPojoMember member) {
    return not(isNullableListItemsMember(member));
  }

  public enum GeneratorOption {
    STANDARD,
    NO_VALIDATION;

    public <T> Predicate<T> validationFilter() {
      return ignore -> this.equals(STANDARD);
    }
  }
}
