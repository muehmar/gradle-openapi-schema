package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.requiredValidationMethodWithAnnotation;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> requiredNullableGetterGenerator(
      GetterType getterType) {
    return GetterGroupsDefinition.create()
        .generator()
        .filter(JavaPojoMember::isRequiredAndNullable)
        .appendSingleBlankLine()
        .append(requiredValidationMethodWithAnnotation().filter(getterType.validationFilter()))
        .append(RefsGenerator.fieldRefs())
        .filter(JavaPojoMember::isRequiredAndNullable);
  }
}
