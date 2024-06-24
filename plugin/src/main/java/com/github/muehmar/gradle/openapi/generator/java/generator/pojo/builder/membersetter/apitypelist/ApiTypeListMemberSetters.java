package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.nullableitemslist.ApiTypeListNullableItemsListOverloadMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Function;

/** Setters for lists with api types (either the list itself or the item type). */
public class ApiTypeListMemberSetters {
  private ApiTypeListMemberSetters() {}

  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
            AllMemberSetter.fromMember(member),
            RequiredNullableMemberSetter.fromMember(member),
            OptionalNotNullableMemberSetter.fromMember(member),
            OptionalNullableMemberSetter.fromMember(member))
        .flatMapOptional(Function.identity())
        .concat(ApiTypeListNullableItemsListOverloadMemberSetters.fromMember(member));
  }
}
