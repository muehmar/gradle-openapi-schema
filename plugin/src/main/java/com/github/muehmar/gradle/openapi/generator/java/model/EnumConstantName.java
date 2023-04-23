package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumConstantName {
  private final String originalConstant;

  private EnumConstantName(String originalConstant) {
    this.originalConstant = originalConstant;
  }

  public static EnumConstantName ofString(String originalConstant) {
    return new EnumConstantName(originalConstant);
  }

  public String getOriginalConstant() {
    return originalConstant;
  }

  public JavaIdentifier asJavaConstant() {
    return JavaIdentifier.fromName(toUpperCaseSnakeCase(originalConstant));
  }

  private static Name toUpperCaseSnakeCase(String name) {
    if (name.toUpperCase().equals(name)) {
      return Name.ofString(name);
    }

    final String converted =
        name.trim()
            .replaceAll("([A-Z])", "_$1")
            .toUpperCase()
            .replaceFirst("^_", "")
            .replaceAll("_+", "_");
    return Name.ofString(converted);
  }
}
