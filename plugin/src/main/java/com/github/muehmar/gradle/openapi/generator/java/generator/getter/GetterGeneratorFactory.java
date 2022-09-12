package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGeneratorFactory {
  private GetterGeneratorFactory() {}

  public static Generator<JavaPojoMember, PojoSettings> create() {
    return GetterGenerator.generator(
        RequiredNullableGetter.getter(),
        RequiredNotNullableGetter.getter(),
        OptionalNullableGetter.getter(),
        OptionalNotNullableGetter.getter());
  }
}
