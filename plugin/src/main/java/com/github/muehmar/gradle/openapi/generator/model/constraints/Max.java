package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.util.Objects;

/** Maximum constraint of a property */
public class Max {
  private final long value;

  public Max(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Max max = (Max) o;
    return value == max.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Max{" + "value=" + value + '}';
  }
}
