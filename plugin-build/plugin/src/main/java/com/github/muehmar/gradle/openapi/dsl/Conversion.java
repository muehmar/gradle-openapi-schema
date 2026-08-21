package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;

@Data
public class Conversion implements Serializable {
  String fromCustomType;
  String toCustomType;

  public Optional<TypeConversion> toTypeConversion() {
    if (fromCustomType == null || toCustomType == null) {
      return Optional.empty();
    }
    return Optional.of(new TypeConversion(fromCustomType, toCustomType));
  }

  void assertValidConfig(String mappingIdentifier) {
    if (fromCustomType == null ^ toCustomType == null) {
      final String missingConfig =
          fromCustomType == null ? "fromCustomType is missing" : "toCustomType is missing";
      throw new OpenApiGeneratorException(
          "Invalid configuration for conversion for %s: Both "
              + "fromCustomType and toCustomType must be set but %s.",
          mappingIdentifier, missingConfig);
    }
  }
}
