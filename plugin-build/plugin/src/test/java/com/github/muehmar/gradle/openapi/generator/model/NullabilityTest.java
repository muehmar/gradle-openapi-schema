package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NullabilityTest {

  @Test
  void isNullable_when_called_then_correct() {
    assertTrue(Nullability.NULLABLE.isNullable());
    assertFalse(Nullability.NOT_NULLABLE.isNullable());
  }

  @Test
  void isNotNullable_when_called_then_correct() {
    assertFalse(Nullability.NULLABLE.isNotNullable());
    assertTrue(Nullability.NOT_NULLABLE.isNotNullable());
  }

  @ParameterizedTest
  @CsvSource({
    "NULLABLE,NULLABLE,NULLABLE",
    "NOT_NULLABLE,NULLABLE,NULLABLE",
    "NULLABLE,NOT_NULLABLE,NULLABLE",
    "NOT_NULLABLE,NOT_NULLABLE,NOT_NULLABLE"
  })
  void leastRestrictive_when_twoValues_then_matchExpected(
      Nullability n1, Nullability n2, Nullability expected) {
    assertEquals(expected, Nullability.leastRestrictive(n1, n2));
  }

  @ParameterizedTest
  @CsvSource({
    "NULLABLE,NULLABLE,NULLABLE",
    "NOT_NULLABLE,NULLABLE,NOT_NULLABLE",
    "NULLABLE,NOT_NULLABLE,NOT_NULLABLE",
    "NOT_NULLABLE,NOT_NULLABLE,NOT_NULLABLE"
  })
  void mostRestrictive_when_twoValues_then_matchExpected(
      Nullability n1, Nullability n2, Nullability expected) {
    assertEquals(expected, Nullability.mostRestrictive(n1, n2));
  }
}
