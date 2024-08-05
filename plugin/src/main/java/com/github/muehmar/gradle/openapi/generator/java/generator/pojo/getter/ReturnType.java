package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;

class ReturnType {
  private final JavaType javaType;

  private ReturnType(JavaType javaType) {
    this.javaType = javaType;
  }

  public static ReturnType fromPojoMember(JavaPojoMember member) {
    return new ReturnType(member.getJavaType());
  }

  @Override
  public String toString() {
    return ParameterizedApiClassName.fromJavaType(javaType)
        .map(ParameterizedApiClassName::asString)
        .orElse(javaType.getParameterizedClassName().asString());
  }
}
