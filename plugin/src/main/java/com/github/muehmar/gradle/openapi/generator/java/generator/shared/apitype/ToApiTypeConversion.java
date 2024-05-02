package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import io.github.muehmar.codegenerator.Generator;

public class ToApiTypeConversion {
  private ToApiTypeConversion() {}

  public static Generator<ApiType, Void> toApiTypeConversion(
      String variableName, ConversionGenerationMode mode) {
    return Generator.<ApiType, Void>emptyGen()
        .append(
            TypeConversion.typeConversion(variableName, mode),
            apiType -> apiType.getToApiTypeConversion().getConversionMethod());
  }
}
