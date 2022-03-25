package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;

public class ValidationGenerator {
  private ValidationGenerator() {}

  public static <A> Generator<A, PojoSettings> notNull() {
    return Generator.<A, PojoSettings>ofWriterFunction(w -> w.println("@NotNull"))
        .append(w -> w.ref(JavaValidationRefs.NOT_NULL))
        .filter((ignore, settings) -> settings.isEnableConstraints());
  }
}
