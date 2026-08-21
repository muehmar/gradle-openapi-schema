package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IsNotNullFlagNameTest {

  @Test
  void getName_when_calledForName_then_correctFlagName() {
    final JavaName flagName = IsNotNullFlagName.fromName(JavaName.fromString("lastname")).getName();
    assertEquals("isLastnameNotNull", flagName.asString());
  }
}
