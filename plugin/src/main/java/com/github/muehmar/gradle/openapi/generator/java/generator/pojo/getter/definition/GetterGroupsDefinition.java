package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JAVA_DOC;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.PACKAGE_PRIVATE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.FLAG_VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.JSON_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.LIST_TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.OPTIONAL_OR_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.STANDARD_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.TRISTATE_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterMethod.VALIDATION_GETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.generator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.group;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.groups;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GroupsDefinitionBuilder.nested;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ONE_OF_MEMBER;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class GetterGroupsDefinition {
  private GetterGroupsDefinition() {}

  public static GetterGroups create() {
    return new GetterGroups(
        groups(
            nested(isStandardMemberType(), standardMemberType()),
            nested(isAllOfMemberType(), allOfMemberType()),
            nested(isOneOfOrAnyOfMemberType(), oneOfAnyOfMemberType())));
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
                generator(JSON_GETTER),
                generator(VALIDATION_GETTER),
                generator(FLAG_VALIDATION_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(JSON_GETTER),
                generator(VALIDATION_GETTER),
                generator(FLAG_VALIDATION_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(TRISTATE_GETTER),
                generator(JSON_GETTER),
                generator(VALIDATION_GETTER))),
        nested(
            isArrayType(),
            groups(
                nested(
                    isNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(LIST_STANDARD_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(LIST_TRISTATE_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER))),
                nested(
                    isNotNullableItemsList(),
                    group(JavaPojoMember::isRequiredAndNotNullable, generator(STANDARD_GETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER),
                        generator(FLAG_VALIDATION_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(TRISTATE_GETTER),
                        generator(JSON_GETTER),
                        generator(VALIDATION_GETTER))))));
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
                generator(JSON_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(OPTIONAL_GETTER),
                generator(OPTIONAL_OR_GETTER),
                generator(JSON_GETTER)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(TRISTATE_GETTER),
                generator(JSON_GETTER))),
        nested(
            isArrayType(),
            groups(
                nested(
                    isNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(LIST_STANDARD_GETTER),
                        generator(JSON_GETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(LIST_OPTIONAL_GETTER),
                        generator(LIST_OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(LIST_TRISTATE_GETTER),
                        generator(JSON_GETTER))),
                nested(
                    isNotNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(STANDARD_GETTER, NO_VALIDATION)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(OPTIONAL_GETTER),
                        generator(OPTIONAL_OR_GETTER),
                        generator(JSON_GETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(TRISTATE_GETTER),
                        generator(JSON_GETTER))))));
  }

  private static PList<GetterGroup> oneOfAnyOfMemberType() {
    return groups(
        nested(
            isNotArrayType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(JSON_GETTER),
                generator(STANDARD_GETTER, NO_VALIDATION, PACKAGE_PRIVATE, NO_JAVA_DOC, NO_JSON)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(JSON_GETTER),
                generator(OPTIONAL_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(JSON_GETTER),
                generator(OPTIONAL_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(JSON_GETTER),
                generator(TRISTATE_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC))),
        nested(
            isArrayType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(JSON_GETTER),
                generator(LIST_STANDARD_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(JSON_GETTER),
                generator(LIST_OPTIONAL_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(JSON_GETTER),
                generator(LIST_OPTIONAL_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(JSON_GETTER),
                generator(LIST_TRISTATE_GETTER, PACKAGE_PRIVATE, NO_JAVA_DOC))));
  }

  private static Predicate<JavaPojoMember> isStandardMemberType() {
    return member -> member.getType().equals(OBJECT_MEMBER) || member.getType().equals(ARRAY_VALUE);
  }

  private static Predicate<JavaPojoMember> isAllOfMemberType() {
    return member -> member.getType().equals(ALL_OF_MEMBER);
  }

  private static Predicate<JavaPojoMember> isOneOfOrAnyOfMemberType() {
    return member ->
        member.getType().equals(ONE_OF_MEMBER) || member.getType().equals(ANY_OF_MEMBER);
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
