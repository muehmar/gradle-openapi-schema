package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.Optional;

class OptionalNullableStandardOverloadMemberSetter extends OptionalNullableMemberSetter {
  public OptionalNullableStandardOverloadMemberSetter(JavaPojoMember member) {
    super(member);
  }

  @Override
  public String argumentType() {
    return member
        .getJavaType()
        .getInternalParameterizedClassName()
        .asStringWrappingNullableValueType();
  }

  @Override
  public String memberValue() {
    return String.format("%s(%s)", UnwrapNullableItemsListMethod.METHOD_NAME, member.getName());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(String.format("%s = false;", member.getIsNullFlagName()));
  }
}
