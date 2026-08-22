package com.github.muehmar.gradle.openapi.issues.issue381;

import java.util.Objects;

/** Custom type for properties with {@code type: number, format: float}. */
public class CustomFloat {
  private final Float value;

  private CustomFloat(Float value) {
    this.value = value;
  }

  public static CustomFloat fromFloat(Float value) {
    return new CustomFloat(value);
  }

  public Float getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomFloat that = (CustomFloat) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "CustomFloat{" + "value=" + value + '}';
  }
}
