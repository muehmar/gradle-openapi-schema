package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import java.io.Serializable;
import java.util.Optional;

public enum JavaModifier implements Serializable {
  PRIVATE("private", JavaModifiers.of(io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE)),
  PROTECTED(
      "protected", JavaModifiers.of(io.github.muehmar.codegenerator.java.JavaModifier.PROTECTED)),
  PACKAGE_PRIVATE("package-private", JavaModifiers.empty()),
  PUBLIC("public", JavaModifiers.of(io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC));

  private final String value;
  private final JavaModifiers javaModifiers;

  JavaModifier(String value, JavaModifiers javaModifiers) {
    this.value = value;
    this.javaModifiers = javaModifiers;
  }

  public String getValue() {
    return value;
  }

  public static Optional<JavaModifier> fromString(String value) {
    return PList.of(values()).find(modifier -> modifier.value.equalsIgnoreCase(value));
  }

  public JavaModifiers asJavaModifiers() {
    return javaModifiers;
  }
}
