package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.gradle.internal.impldep.org.junit.Assert.assertFalse;
import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import org.junit.jupiter.api.Test;

class JavaPojoTest {
  @Test
  void isArray_when_called_then_trueOnlyForArrayPojo() {
    assertTrue(JavaPojos.arrayPojo().isArray());
    assertFalse(JavaPojos.enumPojo().isArray());
    assertFalse(JavaPojos.allNecessityAndNullabilityVariants().isArray());
  }

  @Test
  void isEnum_when_called_then_trueOnlyForEnumPojo() {
    assertFalse(JavaPojos.arrayPojo().isEnum());
    assertTrue(JavaPojos.enumPojo().isEnum());
    assertFalse(JavaPojos.allNecessityAndNullabilityVariants().isEnum());
  }

  @Test
  void isNotEnum_when_called_then_falseOnlyForEnumPojo() {
    assertTrue(JavaPojos.arrayPojo().isNotEnum());
    assertFalse(JavaPojos.enumPojo().isNotEnum());
    assertTrue(JavaPojos.allNecessityAndNullabilityVariants().isNotEnum());
  }

  @Test
  void isObject_when_called_then_trueOnlyForObjectPojo() {
    assertFalse(JavaPojos.arrayPojo().isObject());
    assertFalse(JavaPojos.enumPojo().isObject());
    assertTrue(JavaPojos.allNecessityAndNullabilityVariants().isObject());
  }
}
