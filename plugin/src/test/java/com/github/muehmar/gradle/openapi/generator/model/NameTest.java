package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void startUpperCase_when_startsLowercase_then_startsUppercase() {
    final Name field = Name.ofString("field").startUpperCase();
    assertEquals("Field", field.asString());
  }

  @Test
  void startUpperCase_when_startsUppercase_then_isEquals() {
    final Name field = Name.ofString("Field");
    assertEquals(field, field.startUpperCase());
  }
}
