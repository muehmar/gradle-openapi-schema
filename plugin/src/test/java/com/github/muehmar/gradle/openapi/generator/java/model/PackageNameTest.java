package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import org.junit.jupiter.api.Test;

class PackageNameTest {

  @Test
  void qualifiedClassName_when_called_then_correctFormatted() {
    final PackageName packageName = PackageNames.JAVA_LANG;
    final Name qualifiedClassName = packageName.qualifiedClassName(Name.ofString("Integer"));
    assertEquals("java.lang.Integer", qualifiedClassName.asString());
  }
}
