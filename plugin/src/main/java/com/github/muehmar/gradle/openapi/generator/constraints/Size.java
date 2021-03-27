package com.github.muehmar.gradle.openapi.generator.constraints;

import java.util.Objects;
import java.util.Optional;

/** Size constraint of a property. Either {@code min} or {@code max} will be present (or both). */
public class Size {
  private final Integer min;
  private final Integer max;

  private Size(Integer min, Integer max) {
    this.min = min;
    this.max = max;
  }

  public static Size of(int min, int max) {
    return new Size(min, max);
  }

  public static Size ofMin(int min) {
    return new Size(min, null);
  }

  public static Size ofMax(int max) {
    return new Size(null, max);
  }

  public Optional<Integer> getMin() {
    return Optional.ofNullable(min);
  }

  public Optional<Integer> getMax() {
    return Optional.ofNullable(max);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Size size = (Size) o;
    return Objects.equals(min, size.min) && Objects.equals(max, size.max);
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
