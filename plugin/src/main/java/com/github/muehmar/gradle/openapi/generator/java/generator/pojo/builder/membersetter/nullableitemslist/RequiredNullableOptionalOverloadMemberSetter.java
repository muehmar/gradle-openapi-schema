package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import io.github.muehmar.codegenerator.writer.Writer;

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
  public Writer memberAssigment() {
    return fullListAssigmentWriterBuilder()
        .member(member)
        .fieldAssigment()
        .unwrapOptionalList()
        .unmapListTypeNotNecessary()
        .unwrapOptionalListItem()
        .unmapListItemTypeNotNecessary()
        .build();
  }
}
