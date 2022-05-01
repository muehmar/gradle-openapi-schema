package com.github.muehmar.gradle.openapi.generator.java;

import io.github.muehmar.pojoextension.generator.Generator;

/** Candidates for the generator class... */
public class GeneratorUtil {
  private GeneratorUtil() {}

  public static <A, B> Generator<A, B> noSettingsGen(Generator<A, Void> gen) {
    return (d, s, w) -> gen.generate(d, (Void) null, w);
  }
}
