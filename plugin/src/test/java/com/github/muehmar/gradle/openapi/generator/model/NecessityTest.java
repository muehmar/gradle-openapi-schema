package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}
