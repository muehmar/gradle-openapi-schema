package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;

class ListReturnType {
  private final JavaType javaType;

  private ListReturnType(JavaType javaType) {
    this.javaType = javaType;
  }

  public static ListReturnType fromPojoMember(JavaPojoMember member) {
    return new ListReturnType(member.getJavaType());
  }

  @Override
  public String toString() {
    return ParameterizedApiClassName.fromJavaType(javaType)
        .map(ParameterizedApiClassName::asStringWrappingNullableValueType)
        .orElse(javaType.getParameterizedClassName().asStringWrappingNullableValueType());
  }
}
