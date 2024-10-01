package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypemap;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class ApiTypeMapConditions {
  private ApiTypeMapConditions() {}

  public static Predicate<JavaPojoMember> groupCondition() {
    return member ->
        member
            .getJavaType()
            .onMapType()
            .map(mapType -> mapType.getValue().hasApiType() || mapType.hasApiType())
            .orElse(false);
  }
}
