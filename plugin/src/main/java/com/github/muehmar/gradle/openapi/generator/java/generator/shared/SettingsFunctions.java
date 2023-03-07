package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifiers;

public class SettingsFunctions {
  private SettingsFunctions() {}

  public static <T> JavaModifiers validationMethodModifiers(T ignore, PojoSettings settings) {
    return settings.getValidationMethods().getModifier().asJavaModifiers();
  }
}
