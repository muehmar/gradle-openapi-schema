package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNullableGetter(
      GetterType getterType) {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(JavaPojoMember::isOptionalAndNullable);
  }
}
