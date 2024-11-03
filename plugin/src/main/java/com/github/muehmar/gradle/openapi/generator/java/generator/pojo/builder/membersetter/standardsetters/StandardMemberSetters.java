package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.standardsetters;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;

/**
 * Standard setters of properties which do not need special conversion or handling like {@link
 * ApiType} or nullable items in lists.
 */
public class StandardMemberSetters {
  private StandardMemberSetters() {}

  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
        new RequiredNullableMemberSetter(member),
        new OptionalNotNullableMemberSetter(member),
        new OptionalNullableMemberSetter(member));
  }
}
