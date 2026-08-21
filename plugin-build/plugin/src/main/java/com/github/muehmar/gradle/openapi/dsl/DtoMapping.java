package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappingBuilder.fullDtoMappingBuilder;

import java.io.Serializable;
import lombok.Data;

@Data
public class DtoMapping implements Serializable {
  private String dtoName;
  private String customType;
  private final Conversion conversion;
  private boolean disableMissingConversionWarning;

  public DtoMapping() {
    this.conversion = new Conversion();
  }

  public com.github.muehmar.gradle.openapi.generator.settings.DtoMapping toSettingsDtoMapping() {
    return fullDtoMappingBuilder()
        .dtoName(dtoName)
        .customType(customType)
        .disableMissingConversionWarning(disableMissingConversionWarning)
        .typeConversion(conversion.toTypeConversion())
        .build();
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format("dtoMapping with dtoName '%s' and customType '%s'", dtoName, customType));
  }
}
