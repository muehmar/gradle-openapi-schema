package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.util.Objects;

/** Minimum constraint of a property */
public class Min {
  private final long value;

  public Min(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  public String getValueAsLiteralString() {
    return String.format("%dL", value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Min min1 = (Min) o;
    return value == min1.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Min{" + "value=" + value + '}';
  }
}
