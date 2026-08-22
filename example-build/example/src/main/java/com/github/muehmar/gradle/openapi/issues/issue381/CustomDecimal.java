package com.github.muehmar.gradle.openapi.issues.issue381;

import java.util.Objects;

/** Custom type for properties with {@code type: number, format: decimal}. */
public class CustomDecimal {
  private final Double value;

  private CustomDecimal(Double value) {
    this.value = value;
  }

  public static CustomDecimal fromDouble(Double value) {
    return new CustomDecimal(value);
  }

  public Double getValue() {
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
