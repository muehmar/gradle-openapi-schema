package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.standardsetters;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.ApiTypeListConditions;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypemap.ApiTypeMapConditions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class StandardSetterConditions {
  private StandardSetterConditions() {}

  public static Predicate<JavaPojoMember> groupCondition() {
    return ApiTypeListConditions.groupCondition()
        .negate()
        .and(ApiTypeMapConditions.groupCondition().negate())
        .and(member -> member.getJavaType().hasNoApiType());
  }
}
