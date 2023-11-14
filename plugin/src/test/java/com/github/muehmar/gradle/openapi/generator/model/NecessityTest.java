package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NecessityTest {
  @Test
  void isRequired_when_called_then_correct() {
    assertTrue(Necessity.REQUIRED.isRequired());
    assertFalse(Necessity.OPTIONAL.isRequired());
  }

  @Test
  void isOptional_when_called_then_correct() {
    assertFalse(Necessity.REQUIRED.isOptional());
    assertTrue(Necessity.OPTIONAL.isOptional());
  }

  @ParameterizedTest
  @CsvSource({
    "OPTIONAL,OPTIONAL,OPTIONAL",
    "REQUIRED,OPTIONAL,OPTIONAL",
    "OPTIONAL,REQUIRED,OPTIONAL",
    "REQUIRED,REQUIRED,REQUIRED"
  })
  void leastRestrictive_when_twoValues_then_matchExpected(
      Necessity n1, Necessity n2, Necessity expected) {
    assertEquals(expected, Necessity.leastRestrictive(n1, n2));
  }
}
