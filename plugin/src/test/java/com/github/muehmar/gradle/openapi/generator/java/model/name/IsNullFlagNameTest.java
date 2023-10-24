package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IsNullFlagNameTest {

  @Test
  void getName_when_calledForName_then_correctFlagName() {
    final JavaName flagName = IsNullFlagName.fromName(JavaName.fromString("lastname")).getName();
    assertEquals("isLastnameNull", flagName.asString());
  }
}
