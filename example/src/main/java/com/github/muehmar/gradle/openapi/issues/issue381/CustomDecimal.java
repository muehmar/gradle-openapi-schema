package com.github.muehmar.gradle.openapi.issues.issue381;

import java.util.Objects;

/** Custom type for properties with {@code type: number, format: decimal}. */
public class CustomDecimal {
  private final Float value;

  private CustomDecimal(Float value) {
    this.value = value;
  }

  public static CustomDecimal fromFloat(Float value) {
    return new CustomDecimal(value);
  }

  public Float getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomDecimal that = (CustomDecimal) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "CustomDecimal{" + "value=" + value + '}';
  }
}
