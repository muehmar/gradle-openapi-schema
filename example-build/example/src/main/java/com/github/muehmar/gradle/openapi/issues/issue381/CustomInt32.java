package com.github.muehmar.gradle.openapi.issues.issue381;

import java.util.Objects;

/** Custom type for properties with {@code type: integer, format: int32}. */
public class CustomInt32 {
  private final Integer value;

  private CustomInt32(Integer value) {
    this.value = value;
  }

  public static CustomInt32 fromInteger(Integer value) {
    return new CustomInt32(value);
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomInt32 that = (CustomInt32) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "CustomInt32{" + "value=" + value + '}';
  }
}
