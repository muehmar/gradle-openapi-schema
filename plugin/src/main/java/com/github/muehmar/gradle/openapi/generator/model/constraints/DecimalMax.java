package com.github.muehmar.gradle.openapi.generator.model.constraints;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
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

  public static DecimalMax exclusive(String value) {
    return new DecimalMax(value, false);
  }

  public DecimalMax withInclusiveMax(boolean inclusiveMax) {
    return new DecimalMax(value, inclusiveMax);
  }

  public String getValue() {
    return value;
  }

  public boolean isInclusiveMax() {
    return inclusiveMax;
  }
}
