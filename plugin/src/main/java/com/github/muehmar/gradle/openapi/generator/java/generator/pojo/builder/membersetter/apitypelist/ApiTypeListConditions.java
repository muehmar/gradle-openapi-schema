package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import java.util.function.Predicate;

public class ApiTypeListConditions {
  private ApiTypeListConditions() {}

  public static Predicate<JavaPojoMember> groupCondition() {
    return member -> {
      final JavaType javaType = member.getJavaType();
      return javaType
              .onArrayType()
              .map(arrayType -> arrayType.getItemType().hasApiType())
              .orElse(false)
          || javaType.hasApiType();
    };
  }
}
