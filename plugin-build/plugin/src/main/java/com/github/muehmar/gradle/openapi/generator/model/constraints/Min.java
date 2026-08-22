package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Minimum constraint of a property */
public class Min {
  private final long value;

  public Min(long value) {
    this.value = value;
  }

  /**
   * The smallest integer which satisfies an inclusive lower bound of {@code minimum}, i.e. a
   * fractional bound is rounded up towards the valid range ({@code 5.5} yields {@code 6}).
   */
  public static Min inclusive(BigDecimal minimum) {
    return new Min(minimum.setScale(0, RoundingMode.CEILING).longValue());
  }

  /**
   * The smallest integer which satisfies an exclusive lower bound of {@code minimum}. For a
   * fractional bound this is the same as the inclusive one ({@code 5.5} yields {@code 6}), for an
   * integral bound the value itself gets excluded ({@code 5} yields {@code 6}).
   */
  public static Min exclusive(BigDecimal minimum) {
    return new Min(minimum.setScale(0, RoundingMode.FLOOR).longValue() + 1);
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
