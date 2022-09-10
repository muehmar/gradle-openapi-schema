package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class NewGetterGenerator {
  private NewGetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> generator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendConditionally(
            JavaPojoMember::isRequiredAndNullable, NewRequiredNullableGetter.getter())
        .appendConditionally(
            JavaPojoMember::isRequiredAndNotNullable, NewRequiredNotNullableGetter.getter())
        .appendConditionally(
            JavaPojoMember::isOptionalAndNullable, NewOptionalNullableGetter.getter())
        .appendConditionally(
            JavaPojoMember::isOptionalAndNotNullable, NewOptionalNotNullableGetter.getter());
  }
}
