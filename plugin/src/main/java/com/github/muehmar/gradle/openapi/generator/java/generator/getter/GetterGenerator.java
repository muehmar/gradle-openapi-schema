package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> generator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendConditionally(JavaPojoMember::isRequiredAndNullable, RequiredNullableGetter.getter())
        .appendConditionally(
            JavaPojoMember::isRequiredAndNotNullable, RequiredNotNullableGetter.getter())
        .appendConditionally(JavaPojoMember::isOptionalAndNullable, OptionalNullableGetter.getter())
        .appendConditionally(
            JavaPojoMember::isOptionalAndNotNullable, OptionalNotNullableGetter.getter());
  }
}
