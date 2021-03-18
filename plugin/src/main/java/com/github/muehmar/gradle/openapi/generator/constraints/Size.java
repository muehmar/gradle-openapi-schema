package com.github.muehmar.gradle.openapi.generator.constraints;

import java.util.Objects;

/** Size constraint of a property */
public class Size {
  private final int min;
  private final int max;

  public Size(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Size size = (Size) o;
    return min == size.min && max == size.max;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }

  @Override
  public String toString() {
    return "Size{" + "min=" + min + ", max=" + max + '}';
  }
}
