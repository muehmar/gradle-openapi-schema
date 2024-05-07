package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class ApiTypeListConditions {
  private ApiTypeListConditions() {}

  public static Predicate<JavaPojoMember> groupCondition() {
    return member ->
        member
            .getJavaType()
            .onArrayType()
            .map(arrayType -> arrayType.getItemType().hasApiType() || arrayType.hasApiType())
            .orElse(false);
  }
}
