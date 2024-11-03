package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitype;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import java.util.function.Function;

/**
 * Setters for types with an {@link ApiType}. Does not include generic types where value-types have
 * an {@link ApiType}, e.g. maps and lists.
 */
public class ApiTypeMemberSetters {
  private ApiTypeMemberSetters() {}

  public static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.of(
            ApiTypeRequiredNullableMemberSetter.fromMember(member),
            ApiTypeOptionalNotNullableMemberSetter.fromMember(member),
            ApiTypeOptionalNullableMemberSetter.fromMember(member))
        .flatMapOptional(Function.identity());
  }
}
