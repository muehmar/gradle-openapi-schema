package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.notNullableValidationMethodWithAnnotation;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNotNullableGetterGenerator(
      GetterType getterType) {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(JavaPojoMember::isOptionalAndNotNullable)
        .appendSingleBlankLine()
        .append(notNullableValidationMethodWithAnnotation().filter(getterType.validationFilter()))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isOptionalAndNotNullable);
  }
}
