package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.nullableGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.getter.CommonGetter.wrapNullableInOptionalGetterMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class OptionalNotNullableGetter {
  private OptionalNotNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(wrapNullableInOptionalGetterMethod())
        .appendConditionally(isJacksonJson(), Generator.ofWriterFunction(Writer::println))
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .appendConditionally(isJacksonJson(), nullableGetterMethod())
        .append(RefsGenerator.fieldRefs());
  }
}
