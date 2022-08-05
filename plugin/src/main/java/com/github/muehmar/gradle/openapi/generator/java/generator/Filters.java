package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.util.function.BiPredicate;

public class Filters {
  private Filters() {}

  public static <T> BiPredicate<T, PojoSettings> isValidationEnabled() {
    return (ignore, settings) -> settings.isEnableConstraints();
  }

  public static <T> BiPredicate<T, PojoSettings> isJacksonJson() {
    return (ignore, settings) -> settings.isJacksonJson();
  }
}
