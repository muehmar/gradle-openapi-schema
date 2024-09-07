package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

class OptionalNullableTristateOverloadMemberSetter extends OptionalNullableMemberSetter {

  public OptionalNullableTristateOverloadMemberSetter(JavaPojoMember member) {
    super(member);
  }

  @Override
  public String argumentType() {
    return String.format(
        "Tristate<%s>",
        member.getJavaType().getParameterizedClassName().asStringWrappingNullableValueType());
  }

  @Override
  public Writer memberAssigment() {
    return fullListAssigmentWriterBuilder()
        .member(member)
        .fieldAssigment()
        .unwrapTristateList()
        .unmapListTypeNotNecessary()
        .unwrapOptionalListItem()
        .unmapListItemTypeNotNecessary()
        .build();
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.Wrapped.optionalNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return super.getRefs().cons(OpenApiUtilRefs.TRISTATE);
  }
}
