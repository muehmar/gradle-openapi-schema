package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import java.util.Optional;
import lombok.Value;

@Value
public class FormatTypeMapping implements Serializable {
  String formatType;
  String classType;
  TypeConversion typeConversion;

  public FormatTypeMapping(
      String formatType, String classType, Optional<TypeConversion> typeConversion) {
    this.formatType = formatType;
    this.classType = classType;
    this.typeConversion = typeConversion.orElse(null);
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }
}
