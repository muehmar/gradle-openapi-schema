package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import java.io.Serializable;
import java.util.Optional;

public enum JavaModifier implements Serializable {
  PRIVATE("private"),
  PROTECTED("protected"),
  PACKAGE_PRIVATE("package-private"),
  PUBLIC("public");

  private final String value;

  JavaModifier(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional<JavaModifier> fromString(String value) {
    return PList.of(values()).find(modifier -> modifier.value.equalsIgnoreCase(value));
  }
}
