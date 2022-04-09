package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.nullableGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.wrapNullableInOptionalGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiPredicate;

public class RequiredNullableGetter {
  private RequiredNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    final BiPredicate<PojoMember, PojoSettings> isJacksonJsonOrValidation =
        (f, settings) -> settings.isJacksonJson() || settings.isEnableConstraints();

    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterMethod())
        .appendConditionally(isJacksonJsonOrValidation, Generator.ofWriterFunction(Writer::println))
        .append(nullableGetterMethodWithAnnotations(isJacksonJsonOrValidation))
        .append(RefsGenerator.fieldRefs());
  }

  public static Generator<PojoMember, PojoSettings> nullableGetterMethodWithAnnotations(
      BiPredicate<PojoMember, PojoSettings> isJacksonJsonOrValidation) {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(validationAnnotations())
        .append(jsonProperty())
        .appendConditionally(isJacksonJsonOrValidation, nullableGetterMethod());
  }
}
