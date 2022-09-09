package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.gradle.internal.impldep.org.junit.Assert.assertFalse;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class JavaPojoTest {
  @Test
  void isArray_when_called_then_trueOnlyForArrayPojo() {
    assertTrue(JavaPojos.arrayPojo().isArray());
    assertFalse(JavaPojos.enumPojo().isArray());
    assertFalse(JavaPojos.allNecessityAndNullabilityVariants().isArray());
  }
}
