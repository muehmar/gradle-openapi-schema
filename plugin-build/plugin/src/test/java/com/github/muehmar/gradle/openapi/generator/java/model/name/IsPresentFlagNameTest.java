package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IsPresentFlagNameTest {

  @Test
  void getName_when_calledForName_then_correctFlagName() {
    final JavaName flagName = IsPresentFlagName.fromName(JavaName.fromString("lastname")).getName();
    assertEquals("isLastnamePresent", flagName.asString());
  }
}
