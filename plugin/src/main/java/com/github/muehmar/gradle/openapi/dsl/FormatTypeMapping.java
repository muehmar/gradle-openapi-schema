package com.github.muehmar.gradle.openapi.dsl;

import java.io.Serializable;
import java.util.Objects;

public class FormatTypeMapping implements Serializable {
  private String formatType;
  private String classType;
  private String imports;

  public void setFormatType(String formatType) {
    this.formatType = formatType;
  }

  public void setClassType(String classType) {
    this.classType = classType;
  }

  public void setImports(String imports) {
    this.imports = imports;
  }

  public com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping
      toSettingsFormatTypeMapping() {
    return new com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping(
        formatType, classType, imports);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FormatTypeMapping that = (FormatTypeMapping) o;
    return Objects.equals(formatType, that.formatType)
        && Objects.equals(classType, that.classType)
        && Objects.equals(imports, that.imports);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formatType, classType, imports);
  }

  @Override
  public String toString() {
    return "FormatTypeMapping{"
        + "formatType='"
        + formatType
        + '\''
        + ", classType='"
        + classType
        + '\''
        + ", imports='"
        + imports
        + '\''
        + '}';
  }
}
