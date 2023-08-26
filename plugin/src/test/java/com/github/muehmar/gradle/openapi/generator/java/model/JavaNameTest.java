package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JavaNameTest {

  @Test
  void prefixedMethodeName_when_emptyPrefix_then_startingLowercaseName() {
    final JavaName name = JavaName.fromString("Name");

    final JavaIdentifier prefixedMethodeName = name.prefixedMethodeName("");

    assertEquals("name", prefixedMethodeName.asString());
  }

  @Test
  void prefixedMethodeName_when_hasPrefix_then_startingUppercasePrefixedName() {
    final JavaName name = JavaName.fromString("name");

    final JavaIdentifier prefixedMethodeName = name.prefixedMethodeName("set");

    assertEquals("setName", prefixedMethodeName.asString());
  }
}
