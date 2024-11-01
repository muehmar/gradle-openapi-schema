package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import ch.bluecare.commons.data.PList;
import lombok.Value;

@Value
public class SetterGeneratorSettings {
  PList<SetterGeneratorSetting> settings;

  public static SetterGeneratorSettings empty() {
    return new SetterGeneratorSettings(PList.empty());
  }
}
