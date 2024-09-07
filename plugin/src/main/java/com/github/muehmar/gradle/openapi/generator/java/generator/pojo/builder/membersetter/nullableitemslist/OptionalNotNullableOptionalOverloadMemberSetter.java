package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import io.github.muehmar.codegenerator.writer.Writer;

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
