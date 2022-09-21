package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.util.Objects;

public class DecimalMin {
  private final String value;
  private final boolean inclusiveMin;

  public DecimalMin(String value, boolean inclusiveMin) {
    this.value = value;
    this.inclusiveMin = inclusiveMin;
  }

  public static DecimalMin inclusive(String value) {
    return new DecimalMin(value, true);
  }

  public DecimalMin withInclusiveMin(boolean inclusiveMin) {
    return new DecimalMin(value, inclusiveMin);
  }

  public String getValue() {
    return value;
  }

  public boolean isInclusiveMin() {
    return inclusiveMin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DecimalMin that = (DecimalMin) o;
    return inclusiveMin == that.inclusiveMin && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, inclusiveMin);
  }

  @Override
  public String toString() {
    return "DecimalMin{" + "value='" + value + '\'' + ", inclusiveMin=" + inclusiveMin + '}';
  }
}
