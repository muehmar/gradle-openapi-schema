package com.github.muehmar.gradle.openapi.generator.settings;

import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import java.util.Objects;

public class FormatTypeMapping {
  private final String formatType;
  private final String classType;
  private final String imports;

  public FormatTypeMapping(String formatType, String classType, String imports) {
    this.formatType = formatType;
    this.classType = classType;
    this.imports = imports;
  }

  public static FormatTypeMapping fromExtension(
      OpenApiSchemaGeneratorExtension.FormatTypeMapping ext) {
    return new FormatTypeMapping(ext.getFormatType(), ext.getClassType(), ext.getImports());
  }

  public String getFormatType() {
    return formatType;
  }

  public String getClassType() {
    return classType;
  }

  public String getImports() {
    return imports;
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
