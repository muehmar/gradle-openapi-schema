package com.github.muehmar.gradle.openapi.generator.model.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MultipleOfTest {

  @ParameterizedTest
  @ValueSource(strings = {"1763", "1763.215"})
  void asIntegerLiteralString_when_differentInput_then_correctIntegerStringDroppingFractionalPart(
      String value) {
    final MultipleOf multipleOf = new MultipleOf(new BigDecimal(value));
    assertEquals("1763L", multipleOf.asIntegerLiteralString());
  }
}
