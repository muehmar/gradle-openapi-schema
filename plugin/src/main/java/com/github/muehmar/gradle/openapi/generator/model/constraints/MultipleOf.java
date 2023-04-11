package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class MultipleOf {
  BigDecimal value;

  public String asString() {
    return value.toString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
