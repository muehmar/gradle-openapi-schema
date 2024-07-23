package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupBuilder.fullGetterGroupBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FLAG_VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FRAMEWORK_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.TRISTATE_JSON_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class GetterGroupsDefinition {
  private GetterGroupsDefinition() {}

  public static GetterGroups create() {
    return new GetterGroups(
        PList.of(
            requiredNotNullable(),
            requiredNullable(),
            optionalNotNullable(),
            optionalNullable(),
            allOfRequiredNotNullable(),
            allOfRequiredNullable(),
            allOfOptionalNotNullable(),
            allOfOptionalNullable(),
            arrayTypeRequiredNotNullable(),
            arrayTypeRequiredNullable(),
            arrayTypeOptionalNotNullable(),
            arrayTypeOptionalNullable(),
            allOfArrayTypeRequiredNotNullable(),
            allOfArrayTypeRequiredNullable(),
            allOfArrayTypeOptionalNotNullable(),
            allOfArrayTypeOptionalNullable()));
  }

  private static GetterGroup requiredNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(standardMember().and(JavaPojoMember::isRequiredAndNotNullable))
        .generators(generator(STANDARD_GETTER))
        .build();
  }

  private static GetterGroup requiredNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(standardMember().and(JavaPojoMember::isRequiredAndNullable))
        .generators(
            generator(OPTIONAL_GETTER),
            generator(OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup optionalNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(standardMember().and(JavaPojoMember::isOptionalAndNotNullable))
        .generators(
            generator(OPTIONAL_GETTER),
            generator(OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup optionalNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(standardMember().and(JavaPojoMember::isOptionalAndNullable))
        .generators(
            generator(TRISTATE_GETTER),
            generator(FRAMEWORK_GETTER, NO_JSON),
            generator(TRISTATE_JSON_GETTER))
        .build();
  }

  private static GetterGroup allOfRequiredNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(allOfMember().and(JavaPojoMember::isRequiredAndNotNullable))
        .generators(generator(STANDARD_GETTER, NO_VALIDATION))
        .build();
  }

  private static GetterGroup allOfRequiredNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(allOfMember().and(JavaPojoMember::isRequiredAndNullable))
        .generators(
            generator(OPTIONAL_GETTER),
            generator(OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION))
        .build();
  }

  private static GetterGroup allOfOptionalNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(allOfMember().and(JavaPojoMember::isOptionalAndNotNullable))
        .generators(
            generator(OPTIONAL_GETTER),
            generator(OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION))
        .build();
  }

  private static GetterGroup allOfOptionalNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(allOfMember().and(JavaPojoMember::isOptionalAndNullable))
        .generators(
            generator(TRISTATE_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION),
            generator(TRISTATE_JSON_GETTER))
        .build();
  }

  private static GetterGroup arrayTypeRequiredNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isArrayTypeMember().and(JavaPojoMember::isRequiredAndNotNullable))
        .generators(generator(LIST_STANDARD_GETTER), generator(FRAMEWORK_GETTER))
        .build();
  }

  private static GetterGroup arrayTypeRequiredNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isArrayTypeMember().and(JavaPojoMember::isRequiredAndNullable))
        .generators(
            generator(LIST_OPTIONAL_GETTER),
            generator(LIST_OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup arrayTypeOptionalNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isArrayTypeMember().and(JavaPojoMember::isOptionalAndNotNullable))
        .generators(
            generator(LIST_OPTIONAL_GETTER),
            generator(LIST_OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup arrayTypeOptionalNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isArrayTypeMember().and(JavaPojoMember::isOptionalAndNullable))
        .generators(
            generator(LIST_TRISTATE_GETTER),
            generator(FRAMEWORK_GETTER, NO_JSON),
            generator(TRISTATE_JSON_GETTER))
        .build();
  }

  private static GetterGroup allOfArrayTypeRequiredNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isAllOfArrayTypeMember().and(JavaPojoMember::isRequiredAndNotNullable))
        .generators(generator(LIST_STANDARD_GETTER), generator(FRAMEWORK_GETTER, NO_VALIDATION))
        .build();
  }

  private static GetterGroup allOfArrayTypeRequiredNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isAllOfArrayTypeMember().and(JavaPojoMember::isRequiredAndNullable))
        .generators(
            generator(LIST_OPTIONAL_GETTER),
            generator(LIST_OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup allOfArrayTypeOptionalNotNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isAllOfArrayTypeMember().and(JavaPojoMember::isOptionalAndNotNullable))
        .generators(
            generator(LIST_OPTIONAL_GETTER),
            generator(LIST_OPTIONAL_OR_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION),
            generator(FLAG_VALIDATION_GETTER))
        .build();
  }

  private static GetterGroup allOfArrayTypeOptionalNullable() {
    return fullGetterGroupBuilder()
        .memberFilter(isAllOfArrayTypeMember().and(JavaPojoMember::isOptionalAndNullable))
        .generators(
            generator(LIST_TRISTATE_GETTER),
            generator(FRAMEWORK_GETTER, NO_VALIDATION),
            generator(TRISTATE_JSON_GETTER))
        .build();
  }

  private static Predicate<JavaPojoMember> standardMember() {
    return isStandardMemberType().and(isNotArrayType()).and(hasNoApiType());
  }

  private static Predicate<JavaPojoMember> allOfMember() {
    return isAllOfMemberType().and(isNotArrayType()).and(hasApiType());
  }

  private static Predicate<JavaPojoMember> isArrayTypeMember() {
    return isStandardMemberType().and(isArrayType()).and(hasNoApiType());
  }

  private static Predicate<JavaPojoMember> isAllOfArrayTypeMember() {
    return isAllOfMemberType().and(isArrayType()).and(hasApiType());
  }

  private static Predicate<JavaPojoMember> isStandardMemberType() {
    return member -> member.getType().equals(OBJECT_MEMBER) || member.getType().equals(ARRAY_VALUE);
  }

  private static Predicate<JavaPojoMember> isAllOfMemberType() {
    return member -> member.getType().equals(ALL_OF_MEMBER);
  }

  private static Predicate<JavaPojoMember> hasApiType() {
    return member -> member.getJavaType().hasApiType();
  }

  private static Predicate<JavaPojoMember> hasNoApiType() {
    return hasApiType().negate();
  }

  private static Predicate<JavaPojoMember> isArrayType() {
    return member -> member.getJavaType().isArrayType();
  }

  private static Predicate<JavaPojoMember> isNotArrayType() {
    return isArrayType().negate();
  }

  private static GetterGenerator generator(GetterMethod getterMethod) {
    return new GetterGenerator(getterMethod, GetterGeneratorSettings.empty());
  }

  private static GetterGenerator generator(
      GetterMethod getterMethod, GetterGeneratorSetting... settings) {
    return new GetterGenerator(
        getterMethod, new GetterGeneratorSettings(PList.fromArray(settings)));
  }
}
