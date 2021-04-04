package com.github.muehmar.gradle.openapi.generator.constraints;

import java.util.Objects;

public class DecimalMax {
  private final String value;
  private final boolean inclusiveMax;

  public DecimalMax(String value, boolean inclusiveMax) {
    this.value = value;
    this.inclusiveMax = inclusiveMax;
  }

  public static DecimalMax inclusive(String value) {
    return new DecimalMax(value, true);
  }

  public DecimalMax exclusiveMax() {
    return new DecimalMax(value, false);
  }

  public String getValue() {
    return value;
  }

  public boolean isInclusiveMax() {
    return inclusiveMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DecimalMax that = (DecimalMax) o;
    return inclusiveMax == that.inclusiveMax && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, inclusiveMax);
  }

  @Override
  public String toString() {
    return "DecimalMax{" + "value='" + value + '\'' + ", inclusiveMax=" + inclusiveMax + '}';
  }
}
