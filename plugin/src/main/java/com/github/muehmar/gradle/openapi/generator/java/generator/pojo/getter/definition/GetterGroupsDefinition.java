package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
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
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.generator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.group;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.groups;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.nested;
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
        groups(
            nested(isStandardMemberType(), standardMemberType()),
            nested(isAllOfMemberType(), allOfMemberType())));
  }

  private static PList<GetterGroup> standardMemberType() {
    return groups(
        nested(
            isNotArrayType(),
            group(JavaPojoMember::isRequiredAndNotNullable, generator(STANDARD_GETTER)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER),
                generator(FLAG_VALIDATION_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER),
                generator(FLAG_VALIDATION_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(TRISTATE_GETTER),
                generator(TRISTATE_JSON_GETTER),
                generator(FRAMEWORK_GETTER, NO_JSON))),
        nested(
            isArrayType(),
            groups(
                nested(
                    isNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(LIST_STANDARD_GETTER),
                        generator(FRAMEWORK_GETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(FRAMEWORK_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(FRAMEWORK_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(LIST_TRISTATE_GETTER),
                        generator(TRISTATE_JSON_GETTER),
                        generator(FRAMEWORK_GETTER, NO_JSON))),
                nested(
                    isNotNullableItemsList(),
                    group(JavaPojoMember::isRequiredAndNotNullable, generator(STANDARD_GETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(FRAMEWORK_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(FRAMEWORK_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(TRISTATE_GETTER),
                        generator(TRISTATE_JSON_GETTER),
                        generator(FRAMEWORK_GETTER, NO_JSON))))));
  }

  private static PList<GetterGroup> allOfMemberType() {
    return groups(
        nested(
            isNotArrayType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(STANDARD_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(TRISTATE_GETTER),
                generator(TRISTATE_JSON_GETTER))),
        nested(
            isArrayType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(LIST_STANDARD_GETTER),
                generator(FRAMEWORK_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(LIST_OPTIONAL_GETTER),
                generator(LIST_OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(LIST_OPTIONAL_GETTER),
                generator(LIST_OPTIONAL_OR_GETTER),
                generator(FRAMEWORK_GETTER, NO_VALIDATION)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(LIST_TRISTATE_GETTER),
                generator(TRISTATE_JSON_GETTER))));
  }

  private static Predicate<JavaPojoMember> isStandardMemberType() {
    return member -> member.getType().equals(OBJECT_MEMBER) || member.getType().equals(ARRAY_VALUE);
  }

  private static Predicate<JavaPojoMember> isAllOfMemberType() {
    return member -> member.getType().equals(ALL_OF_MEMBER);
  }

  private static Predicate<JavaPojoMember> isNullableItemsList() {
    return member -> member.getJavaType().isNullableItemsArrayType();
  }

  private static Predicate<JavaPojoMember> isNotNullableItemsList() {
    return isNullableItemsList().negate();
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
}
