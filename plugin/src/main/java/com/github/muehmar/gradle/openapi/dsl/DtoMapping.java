package com.github.muehmar.gradle.openapi.dsl;

import java.io.Serializable;
import lombok.Data;

@Data
public class DtoMapping implements Serializable {
  private String dtoName;
  private String customType;
  private final Conversion conversion;

  public DtoMapping() {
    this.conversion = new Conversion();
  }

  public com.github.muehmar.gradle.openapi.generator.settings.DtoMapping toSettingsDtoMapping() {
    return new com.github.muehmar.gradle.openapi.generator.settings.DtoMapping(
        dtoName, customType, conversion.toTypeConversion());
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format("dtoMapping with dtoName '%s' and customType '%s'", dtoName, customType));
  }
}
