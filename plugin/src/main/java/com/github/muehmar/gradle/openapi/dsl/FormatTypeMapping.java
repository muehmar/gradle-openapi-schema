package com.github.muehmar.gradle.openapi.dsl;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FormatTypeMapping implements Serializable {
  private String formatType;
  private String classType;

  public void setFormatType(String formatType) {
    this.formatType = formatType;
  }

  public void setClassType(String classType) {
    this.classType = classType;
  }

  public com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping
      toSettingsFormatTypeMapping() {
    return new com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping(
        formatType, classType);
  }
}
