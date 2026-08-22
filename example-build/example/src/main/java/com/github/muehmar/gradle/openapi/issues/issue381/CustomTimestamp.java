package com.github.muehmar.gradle.openapi.issues.issue381;

import java.util.Objects;

/** Custom type for properties with {@code type: integer, format: timestamp}. */
public class CustomTimestamp {
  private final Integer value;

  private CustomTimestamp(Integer value) {
    this.value = value;
  }

  public static CustomTimestamp fromInteger(Integer value) {
    return new CustomTimestamp(value);
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomTimestamp that = (CustomTimestamp) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "CustomTimestamp{" + "value=" + value + '}';
  }
}
