package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

class OptionalNullableStandardOverloadMemberSetter extends OptionalNullableMemberSetter {
  public OptionalNullableStandardOverloadMemberSetter(JavaPojoMember member) {
    super(member);
  }

  @Override
  public String argumentType() {
    return member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType();
  }

  @Override
  public Writer memberAssigment() {
    return fullListAssigmentWriterBuilder()
        .member(member)
        .unwrapListNotNecessary()
        .unmapListTypeNotNecessary()
        .unwrapOptionalListItem()
        .unmapListItemTypeNotNecessary()
        .build();
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(String.format("%s = false;", member.getIsNullFlagName()));
  }
}
