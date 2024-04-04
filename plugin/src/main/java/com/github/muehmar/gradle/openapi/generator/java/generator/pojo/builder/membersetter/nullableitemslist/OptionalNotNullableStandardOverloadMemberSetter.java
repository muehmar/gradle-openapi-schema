package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;

class OptionalNotNullableStandardOverloadMemberSetter extends OptionalNotNullableMemberSetter {

  public OptionalNotNullableStandardOverloadMemberSetter(JavaPojoMember member) {
    super(member);
  }

  @Override
  public String argumentType() {
    return member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType();
  }

  @Override
  public String memberValue() {
    return String.format("%s(%s)", UnwrapNullableItemsListMethod.METHOD_NAME, member.getName());
  }
}
