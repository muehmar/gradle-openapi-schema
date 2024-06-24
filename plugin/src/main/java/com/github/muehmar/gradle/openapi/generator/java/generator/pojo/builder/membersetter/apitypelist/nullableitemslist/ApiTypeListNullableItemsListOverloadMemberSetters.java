package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.nullableitemslist;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Function;

public class ApiTypeListNullableItemsListOverloadMemberSetters {
  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
            RequiredNotNullableStandardOverloadMemberSetter.fromMember(member),
            RequiredNullableStandardOverloadMemberSetter.fromMember(member),
            RequiredNullableOptionalOverloadMemberSetter.fromMember(member),
            OptionalNotNullableStandardOverloadMemberSetter.fromMember(member))
        .flatMapOptional(Function.identity());
  }
}
