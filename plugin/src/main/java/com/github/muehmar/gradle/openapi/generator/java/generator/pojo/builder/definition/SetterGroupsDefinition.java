package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.GroupsDefinitionBuilder.generator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.GroupsDefinitionBuilder.group;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.GroupsDefinitionBuilder.groups;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.GroupsDefinitionBuilder.nested;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_NULLABLE_VALUE_STANDARD_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_NULLABLE_VALUE_TRISTATE_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_OPTIONAL_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_STANDARD_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.CONTAINER_TRISTATE_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.JSON_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.OPTIONAL_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.STANDARD_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterMethod.TRISTATE_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ONE_OF_MEMBER;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class SetterGroupsDefinition {
  private SetterGroupsDefinition() {}

  public static SetterGroups create() {
    return new SetterGroups(
        groups(
            nested(isStandardMemberType().or(isAllOfMemberType()), standardAndAllOfMemberType()),
            nested(isOneOfOrAnyOfMemberType(), oneOfAnyOfMemberType())));
  }

  private static PList<SetterGroup> standardAndAllOfMemberType() {
    return groups(
        nested(
            isNotContainerType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(OPTIONAL_SETTER)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(OPTIONAL_SETTER)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(TRISTATE_SETTER))),
        nested(
            isContainerType(),
            groups(
                nested(
                    isNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_TRISTATE_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_TRISTATE_SETTER))),
                nested(
                    isNotNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_TRISTATE_SETTER))))));
  }

  private static PList<SetterGroup> oneOfAnyOfMemberType() {
    return groups(
        nested(
            isNotContainerType(),
            group(
                JavaPojoMember::isRequiredAndNotNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER)),
            group(
                JavaPojoMember::isRequiredAndNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(OPTIONAL_SETTER)),
            group(
                JavaPojoMember::isOptionalAndNotNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(OPTIONAL_SETTER)),
            group(
                JavaPojoMember::isOptionalAndNullable,
                generator(JSON_SETTER),
                generator(STANDARD_SETTER),
                generator(TRISTATE_SETTER))),
        nested(
            isContainerType(),
            groups(
                nested(
                    isNotNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_TRISTATE_SETTER))),
                nested(
                    isNullableItemsList(),
                    group(
                        JavaPojoMember::isRequiredAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER)),
                    group(
                        JavaPojoMember::isRequiredAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNotNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_OPTIONAL_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER)),
                    group(
                        JavaPojoMember::isOptionalAndNullable,
                        generator(JSON_SETTER),
                        generator(CONTAINER_STANDARD_SETTER),
                        generator(CONTAINER_TRISTATE_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_STANDARD_SETTER),
                        generator(CONTAINER_NULLABLE_VALUE_TRISTATE_SETTER))))));
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

  private static Predicate<JavaPojoMember> isContainerType() {
    return member -> member.getJavaType().isArrayType() || member.getJavaType().isMapType();
  }

  private static Predicate<JavaPojoMember> isNotContainerType() {
    return isContainerType().negate();
  }

  private static Predicate<JavaPojoMember> hasApiTypeDeep() {
    return member -> member.getJavaType().hasApiTypeDeep();
  }

  private static Predicate<JavaPojoMember> hasNoApiTypeDeep() {
    return hasApiTypeDeep().negate();
  }
}
