package com.github.muehmar.gradle.openapi.generator.data;

/** Defines the necessity of an attribute, i.e. if the attribute is required or optional. */
public enum Necessity {
  REQUIRED,
  OPTIONAL;

  public static Necessity fromBoolean(boolean necessity) {
    return necessity ? REQUIRED : OPTIONAL;
  }
}
