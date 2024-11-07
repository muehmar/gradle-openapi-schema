package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMappingBuilder.fullFormatTypeMappingBuilder;

import java.io.Serializable;
import lombok.Data;

@Data
public class FormatTypeMapping implements Serializable {
  private String formatType;
  private String classType;
  private final Conversion conversion;
  private boolean disableMissingConversionWarning;

  public FormatTypeMapping() {
    this.conversion = new Conversion();
  }

  public com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping
      toSettingsFormatTypeMapping() {
    return fullFormatTypeMappingBuilder()
        .formatType(formatType)
        .classType(classType)
        .disableMissingConversionWarning(disableMissingConversionWarning)
        .typeConversion(conversion.toTypeConversion())
        .build();
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format(
            "formatTypeMapping with formatType '%s' and classType '%s'", formatType, classType));
  }
}
