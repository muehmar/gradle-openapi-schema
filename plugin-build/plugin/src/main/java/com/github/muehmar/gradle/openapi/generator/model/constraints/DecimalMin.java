package com.github.muehmar.gradle.openapi.generator.model.constraints;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
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

  public static DecimalMin exclusive(String value) {
    return new DecimalMin(value, false);
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
}
