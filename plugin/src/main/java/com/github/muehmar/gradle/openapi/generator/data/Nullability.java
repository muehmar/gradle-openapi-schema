package com.github.muehmar.gradle.openapi.generator.data;

/** Defines if an attribute may be nullable or not. */
public enum Nullability {
  NULLABLE,
  NOT_NULLABLE;

  public static Nullability fromNullableBoolean(boolean nullable) {
    return nullable ? NULLABLE : NOT_NULLABLE;
  }
}