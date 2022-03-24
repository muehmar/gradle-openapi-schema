package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<PojoMember, PojoSettings> generator() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendConditionally(PojoMember::isRequiredAndNullable, RequiredNullableGetter.getter())
        .appendConditionally(
            PojoMember::isRequiredAndNotNullable, RequiredNotNullableGetter.getter())
        .appendConditionally(PojoMember::isOptionalAndNullable, OptionalNullableGetter.getter())
        .appendConditionally(
            PojoMember::isOptionalAndNotNullable, OptionalNotNullableGetter.getter());
  }
}
