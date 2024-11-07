package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@PojoBuilder
public class FormatTypeMapping implements Serializable {
  String formatType;
  String classType;
  @Nullable TypeConversion typeConversion;
  boolean disableMissingConversionWarning;

  public FormatTypeMapping(
      String formatType, String classType, Optional<TypeConversion> typeConversion) {
    this.formatType = formatType;
    this.classType = classType;
    this.typeConversion = typeConversion.orElse(null);
    this.disableMissingConversionWarning = false;
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }
}
