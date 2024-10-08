package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import java.util.Optional;
import lombok.Value;

@Value
public class ClassTypeMapping implements Serializable {
  String fromClass;
  String toClass;
  TypeConversion typeConversion;

  public ClassTypeMapping(
      String fromClass, String toClass, Optional<TypeConversion> typeConversion) {
    this.fromClass = fromClass;
    this.toClass = toClass;
    this.typeConversion = typeConversion.orElse(null);
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }
}
