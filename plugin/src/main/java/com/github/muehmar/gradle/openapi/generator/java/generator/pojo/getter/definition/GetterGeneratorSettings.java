package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;
import java.util.function.Predicate;
import lombok.Value;

@Value
public class GetterGeneratorSettings {
  PList<GetterGeneratorSetting> settings;

  public static GetterGeneratorSettings empty() {
    return new GetterGeneratorSettings(PList.empty());
  }

  public static GetterGeneratorSettings getterGeneratorSettings(
      GetterGeneratorSetting... settings) {
    return new GetterGeneratorSettings(PList.fromArray(settings));
  }

  public <T> Predicate<T> validationFilter() {
    return ignore -> isValidation();
  }

  public boolean isValidation() {
    return settings.filter(GetterGeneratorSetting.NO_VALIDATION::equals).isEmpty();
  }

  public <T> Predicate<T> jsonFilter() {
    return ignore -> isJson();
  }

  public boolean isJson() {
    return settings.filter(GetterGeneratorSetting.NO_JSON::equals).isEmpty();
  }
}
