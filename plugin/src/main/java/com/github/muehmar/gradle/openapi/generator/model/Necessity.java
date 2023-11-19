package com.github.muehmar.gradle.openapi.generator.model;

/** Defines the necessity of an attribute, i.e. if the attribute is required or optional. */
public enum Necessity {
  REQUIRED,
  OPTIONAL;

  public static Necessity fromBoolean(boolean necessity) {
    return necessity ? REQUIRED : OPTIONAL;
  }

  public static Necessity leastRestrictive(Necessity n1, Necessity n2) {
    return n1 == OPTIONAL ? n1 : n2;
  }

  public static Necessity mostRestrictive(Necessity n1, Necessity n2) {
    return n1 == REQUIRED ? n1 : n2;
  }

  public boolean isOptional() {
    return this == OPTIONAL;
  }

  public boolean isRequired() {
    return this == REQUIRED;
  }
}
