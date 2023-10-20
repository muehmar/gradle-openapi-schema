package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import org.junit.jupiter.api.Test;

class IsNullFlagNameTest {

  @Test
  void getName_when_calledForName_then_correctFlagName() {
    final JavaIdentifier flagName = IsNullFlagName.fromName(Name.ofString("lastname")).getName();
    assertEquals("isLastnameNull", flagName.asString());
  }
}