package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class RequiredNotNullableGetter {
  private RequiredNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNotNullableGetterGenerator(
      GetterType getterType) {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(JavaPojoMember::isRequiredAndNotNullable);
  }
}
