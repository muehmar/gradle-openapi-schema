package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Maximum constraint of a property */
public class Max {
  private final long value;

  public Max(long value) {
    this.value = value;
  }

  /**
   * The largest integer which satisfies an inclusive upper bound of {@code maximum}, i.e. a
   * fractional bound is rounded down towards the valid range ({@code 100.5} yields {@code 100}).
   */
  public static Max inclusive(BigDecimal maximum) {
    return new Max(maximum.setScale(0, RoundingMode.FLOOR).longValue());
  }

  /**
   * The largest integer which satisfies an exclusive upper bound of {@code maximum}. For a
   * fractional bound this is the same as the inclusive one ({@code 100.5} yields {@code 100}), for
   * an integral bound the value itself gets excluded ({@code 100} yields {@code 99}).
   */
  public static Max exclusive(BigDecimal maximum) {
    return new Max(maximum.setScale(0, RoundingMode.CEILING).longValue() - 1);
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
