package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;

class RequiredNullableOptionalOverloadMemberSetter extends RequiredNullableMemberSetter {

  public RequiredNullableOptionalOverloadMemberSetter(JavaPojoMember member) {
    super(member);
  }

  @Override
  public String argumentType() {
    return String.format(
        "Optional<%s>",
        member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType());
  }

  @Override
  public String memberValue() {
    return String.format(
        "%s.map(l -> %s(l)).orElse(null)",
        member.getName(), UnwrapNullableItemsListMethod.METHOD_NAME);
  }
}
