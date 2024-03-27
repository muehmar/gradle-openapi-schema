package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;

class OptionalNotNullableOptionalOverloadMemberSetter extends OptionalNotNullableMemberSetter {

  public OptionalNotNullableOptionalOverloadMemberSetter(JavaPojoMember member) {
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
        "%s.map(this::%s).orElse(null)",
        member.getName(), UnwrapNullableItemsListMethod.METHOD_NAME);
  }
}
