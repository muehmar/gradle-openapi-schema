package com.github.muehmar.gradle.openapi.generator.model;

/** Defines the necessity of an attribute, i.e. if the attribute is required or optional. */
public enum Necessity {
  REQUIRED,
  OPTIONAL;

  public static Necessity fromBoolean(boolean necessity) {
    return necessity ? REQUIRED : OPTIONAL;
  }

  public boolean isOptional() {
    return this == OPTIONAL;
  }

  public boolean isRequired() {
    return this == REQUIRED;
  }
}
