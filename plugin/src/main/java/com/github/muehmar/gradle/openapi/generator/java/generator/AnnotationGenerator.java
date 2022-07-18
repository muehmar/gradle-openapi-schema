package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;

public class AnnotationGenerator {
  private AnnotationGenerator() {}

  public static <A, B> Generator<A, B> override() {
    return Generator.ofWriterFunction(w -> w.println("@Override"));
  }

  public static <A> Generator<A, PojoSettings> deprecatedRawGetter() {
    return Generator.<A, PojoSettings>ofWriterFunction(w -> w.println("@Deprecated"))
        .filter((ignore, settings) -> settings.getRawGetter().isDeprecatedAnnotation());
  }
}
