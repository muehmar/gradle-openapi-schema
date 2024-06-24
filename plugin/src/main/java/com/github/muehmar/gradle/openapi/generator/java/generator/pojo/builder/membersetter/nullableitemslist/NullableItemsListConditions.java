package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.ApiTypeListConditions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

public class NullableItemsListConditions {
  private NullableItemsListConditions() {}

  public static Predicate<JavaPojoMember> groupCondition() {
    return ApiTypeListConditions.groupCondition()
        .negate()
        .and(member -> member.getJavaType().isNullableItemsArrayType());
  }
}
