package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.util.function.BiPredicate;

public class Filters {
  private Filters() {}

  public static <T> BiPredicate<T, PojoSettings> isValidationEnabled() {
    return (ignore, settings) -> settings.isEnableValidation();
  }

  public static <T> BiPredicate<T, PojoSettings> isJacksonJson() {
    return (ignore, settings) -> settings.isJacksonJson();
  }

  public static <T> BiPredicate<T, PojoSettings> isJacksonXml() {
    return (ignore, settings) -> settings.isJacksonXml();
  }

  public static <T> BiPredicate<T, PojoSettings> isStagedBuilder() {
    return (ignore, settings) -> settings.isEnableStagedBuilder();
  }

  public static <T> BiPredicate<T, PojoSettings> isJacksonJsonOrValidation() {
    return Filters.<T>isJacksonJson().or(isValidationEnabled());
  }
}
