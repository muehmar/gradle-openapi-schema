package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNullableGetterGenerator(
      GetterType getterType) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            GetterGroupsDefinition.create()
                .generator()
                .filter(JavaPojoMember::isOptionalAndNullable))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isOptionalAndNullable);
  }
}
