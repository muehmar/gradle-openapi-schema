package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class ToApiTypeConversion {
  private ToApiTypeConversion() {}

  public static Generator<ApiType, PojoSettings> toApiTypeConversion(String variableName) {
    return Generator.<ApiType, PojoSettings>emptyGen()
        .append(
            TypeConversion.typeConversion(variableName),
            apiType -> apiType.getToApiTypeConversion().getConversionMethod());
  }
}
