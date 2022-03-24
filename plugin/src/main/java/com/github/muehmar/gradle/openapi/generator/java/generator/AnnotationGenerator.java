package com.github.muehmar.gradle.openapi.generator.java.generator;

import io.github.muehmar.pojoextension.generator.Generator;

public class AnnotationGenerator {
  private AnnotationGenerator() {}

  public static <A, B> Generator<A, B> override() {
    return Generator.ofWriterFunction(w -> w.println("@Override"));
  }
}
