package com.github.muehmar.gradle.openapi.dsl;

import java.io.Serializable;
import lombok.Data;

@Data
public class FormatTypeMapping implements Serializable {
  private String formatType;
  private String classType;
  private final Conversion conversion;

  public FormatTypeMapping() {
    this.conversion = new Conversion();
  }

  public com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping
      toSettingsFormatTypeMapping() {
    return new com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping(
        formatType, classType, conversion.toTypeConversion());
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format(
            "formatTypeMapping with formatType '%s' and classType '%s'", formatType, classType));
  }
}
