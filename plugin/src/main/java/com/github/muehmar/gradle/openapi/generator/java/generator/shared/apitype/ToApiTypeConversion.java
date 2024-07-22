package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class ToApiTypeConversion {
  private ToApiTypeConversion() {}

  public static Writer toApiTypeConversion(
      ApiType apiType, String variableName, ConversionGenerationMode mode) {
    return toApiTypeConversion(variableName, mode).generate(apiType, (Void) null, javaWriter());
  }

  public static Generator<ApiType, Void> toApiTypeConversion(
      String variableName, ConversionGenerationMode mode) {
    return Generator.<ApiType, Void>emptyGen()
        .append(
            TypeConversion.typeConversion(variableName, mode),
            apiType -> apiType.getToApiTypeConversion().getConversionMethod());
  }
}
