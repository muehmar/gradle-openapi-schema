package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
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
  public String memberValue() {
    return String.format(
        "%s.onValue(l -> %s(l)).onNull(() -> null).onAbsent(() -> null)",
        member.getName(), UnwrapNullableItemsListMethod.METHOD_NAME);
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.wrappedOptionalNullableFlagAssignment(member));
  }

  @Override
  public Writer addRefs(Writer writer) {
    return super.addRefs(writer).ref(OpenApiUtilRefs.TRISTATE);
  }
}
