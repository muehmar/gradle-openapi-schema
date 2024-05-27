package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
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
    return Optional.of(FlagAssignments.Wrapped.optionalNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    return super.getRefs().cons(OpenApiUtilRefs.TRISTATE);
  }
}
