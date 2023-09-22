package com.github.muehmar.gradle.openapi.generator.model;

import lombok.EqualsAndHashCode;

/** Represents the name of the schema in the specification. */
@EqualsAndHashCode
public class SchemaName {
  private final String value;

  private SchemaName(String val) {
    this.value = val;
  }

  public static SchemaName ofString(String val) {
    if (val == null || val.trim().isEmpty()) {
      throw new IllegalArgumentException("A schema name must not be null or empty");
    }

    return new SchemaName(val);
  }

  public static SchemaName ofName(Name name) {
    return new SchemaName(name.asString());
  }

  public Name asName() {
    return Name.ofString(value);
  }

  public String asString() {
    return value;
  }

  @Override
  public String toString() {
    return asString();
  }
}
