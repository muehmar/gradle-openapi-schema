package com.github.muehmar.gradle.openapi.generator.model;

/** Defines if an attribute may be nullable or not. */
public enum Nullability {
  NULLABLE,
  NOT_NULLABLE;

  public static Nullability fromNullableBoolean(boolean nullable) {
    return nullable ? NULLABLE : NOT_NULLABLE;
  }

  public static Nullability leastRestrictive(Nullability n1, Nullability n2) {
    return n1 == NULLABLE ? n1 : n2;
  }

  public static Nullability mostRestrictive(Nullability n1, Nullability n2) {
    return n1 == NOT_NULLABLE ? n1 : n2;
  }

  public boolean isNullable() {
    return this == NULLABLE;
  }

  public boolean isNotNullable() {
    return this == NOT_NULLABLE;
  }
}
