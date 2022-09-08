package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaPojoMember {
  private final JavaType javaType;
  private final NewPojoMember pojoMember;

  private JavaPojoMember(JavaType javaType, NewPojoMember pojoMember) {
    this.javaType = javaType;
    this.pojoMember = pojoMember;
  }

  public static JavaPojoMember wrap(NewPojoMember pojoMember, TypeMappings typeMappings) {
    final JavaType javaType = JavaType.wrap(pojoMember.getType(), typeMappings);
    return new JavaPojoMember(javaType, pojoMember);
  }
}
