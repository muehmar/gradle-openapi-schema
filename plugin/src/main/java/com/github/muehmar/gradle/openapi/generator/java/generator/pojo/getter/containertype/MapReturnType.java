package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;

public class MapReturnType {
  private final JavaType javaType;

  private MapReturnType(JavaType javaType) {
    this.javaType = javaType;
  }

  public static MapReturnType fromPojoMember(JavaPojoMember member) {
    return new MapReturnType(member.getJavaType());
  }

  @Override
  public String toString() {
    return javaType.getWriteableParameterizedClassName().asString();
  }
}
