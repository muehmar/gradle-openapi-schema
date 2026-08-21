package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappingBuilder.fullClassTypeMappingBuilder;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import java.io.Serializable;
import lombok.Data;

@Data
public class ClassMapping implements Serializable {
  private String fromClass;
  private String toClass;
  private final Conversion conversion;
  private boolean disableMissingConversionWarning;

  public ClassMapping() {
    this.conversion = new Conversion();
  }

  public ClassTypeMapping toSettingsClassMapping() {
    return fullClassTypeMappingBuilder()
        .fromClass(fromClass)
        .toClass(toClass)
        .disableMissingConversionWarning(disableMissingConversionWarning)
        .typeConversion(conversion.toTypeConversion())
        .build();
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format("classTypeMapping with fromClass '%s' and toClass '%s'", fromClass, toClass));
  }
}
