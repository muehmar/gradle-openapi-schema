package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}
