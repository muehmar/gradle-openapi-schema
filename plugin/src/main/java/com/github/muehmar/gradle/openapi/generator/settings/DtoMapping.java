package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import java.util.Optional;
import lombok.Value;

@Value
public class DtoMapping implements Serializable {
  String dtoName;
  String customType;
  TypeConversion typeConversion;

  public DtoMapping(String dtoName, String customType, Optional<TypeConversion> typeConversion) {
    this.dtoName = dtoName;
    this.customType = customType;
    this.typeConversion = typeConversion.orElse(null);
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }
}
