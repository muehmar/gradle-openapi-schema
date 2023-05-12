package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> generator() {
    return RequiredNotNullableGetter.getter()
        .append(RequiredNullableGetter.getter())
        .append(OptionalNotNullableGetter.getter())
        .append(OptionalNullableGetter.getter());
  }
}
