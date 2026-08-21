package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Key of a member, i.e. if two members have the same key, they represent the same property. They
 * may not necessarily be fully interchangeable, as two members may have different constraints (this
 * also includes necessity and nullability). The two members can be represented by the same
 * technical member but the constraints need to be validated independently.
 */
@EqualsAndHashCode
@ToString
public class MemberKey {
  private final JavaName name;
  private final JavaType javaType;

  private MemberKey(JavaName name, JavaType javaType) {
    this.name = name;
    this.javaType = javaType;
  }

  public static MemberKey memberKey(JavaName name, JavaType javaType) {
    return new MemberKey(name, javaType.withNullability(NOT_NULLABLE));
  }
}
