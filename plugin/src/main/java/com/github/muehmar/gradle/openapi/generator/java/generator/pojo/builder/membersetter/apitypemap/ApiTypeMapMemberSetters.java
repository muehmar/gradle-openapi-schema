package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypemap;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Function;

/** Setters for maps with api types (either the map itself or the value type). */
public class ApiTypeMapMemberSetters {
  private ApiTypeMapMemberSetters() {}

  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(OptionalNullableMemberSetter.fromMember(member))
        .flatMapOptional(Function.identity());
  }
}
