package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;

public class FromApiTypeConversionRenderer {
  private FromApiTypeConversionRenderer() {}

  public static Writer fromApiTypeConversion(
      ApiType apiType, String variableName, ConversionGenerationMode mode) {
    return fromApiTypeConversion(variableName, mode).generate(apiType, (Void) null, javaWriter());
  }

  public static Generator<ApiType, Void> fromApiTypeConversion(
      String variableName, ConversionGenerationMode mode) {
    return Generator.<ApiType, Void>emptyGen()
        .append(
            TypeConversion.typeConversion(variableName, mode),
            apiType ->
                apiType.getFromApiTypeConversion().map(FromApiTypeConversion::getConversionMethod));
  }
}
