package com.github.muehmar.gradle.openapi.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void startUpperCase_when_startsLowercase_then_startsUppercase() {
    final Name field = Name.of("field").startUpperCase();
    assertEquals("Field", field.asString());
  }

  @Test
  void startUpperCase_when_startsUppercase_then_isEquals() {
    final Name field = Name.of("Field");
    assertEquals(field, field.startUpperCase());
  }
}
