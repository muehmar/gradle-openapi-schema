package com.github.muehmar.gradle.openapi.issues.issue266;

import java.util.Objects;

/**
 * A custom type used as the target of a {@code dtoMapping} WITHOUT conversion on a referenced (i.e.
 * {@code $ref}) enum schema. Without a conversion the custom type replaces the generated enum
 * entirely, so no enum api-type conversions ({@code fromValue}/{@code getValue}) must be generated
 * against this class.
 */
public class ColorNoConversion {
  private final String value;

  public ColorNoConversion(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ColorNoConversion color = (ColorNoConversion) o;
    return Objects.equals(value, color.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "ColorNoConversion{value='" + value + "'}";
  }
}
