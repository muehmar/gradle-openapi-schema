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
public class ClassTypeMapping implements Serializable {
  String fromClass;
  String toClass;
  @Nullable TypeConversion typeConversion;
  boolean disableMissingConversionWarning;

  public ClassTypeMapping(
      String fromClass, String toClass, Optional<TypeConversion> typeConversion) {
    this.fromClass = fromClass;
    this.toClass = toClass;
    this.typeConversion = typeConversion.orElse(null);
    this.disableMissingConversionWarning = false;
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }
}
