package com.github.muehmar.gradle.openapi.generator.data;

public enum Necessity {
  REQUIRED,
  OPTIONAL;

  public static Necessity fromBoolean(boolean necessity) {
    return necessity ? REQUIRED : OPTIONAL;
  }
}
