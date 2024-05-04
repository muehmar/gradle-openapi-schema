package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;

/** Overloaded setters for nullable items in lists. */
public class NullableItemsListMemberSetters {
  private NullableItemsListMemberSetters() {}

  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
        new RequiredNotNullableMemberSetter(member),
        new RequiredNullableStandardOverloadMemberSetter(member),
        new RequiredNullableOptionalOverloadMemberSetter(member),
        new OptionalNotNullableStandardOverloadMemberSetter(member),
        new OptionalNotNullableOptionalOverloadMemberSetter(member),
        new OptionalNullableStandardOverloadMemberSetter(member),
        new OptionalNullableTristateOverloadMemberSetter(member));
  }
}
