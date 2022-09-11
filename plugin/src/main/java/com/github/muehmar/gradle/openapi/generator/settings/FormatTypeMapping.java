package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FormatTypeMapping implements Serializable {
  private final String formatType;
  private final String classType;

  public FormatTypeMapping(String formatType, String classType) {
    this.formatType = formatType;
    this.classType = classType;
  }

  public String getFormatType() {
    return formatType;
  }

  public String getClassType() {
    return classType;
  }
}
