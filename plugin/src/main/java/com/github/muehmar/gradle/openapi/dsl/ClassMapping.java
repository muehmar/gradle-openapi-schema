package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import java.io.Serializable;
import lombok.Data;

@Data
public class ClassMapping implements Serializable {
  private String fromClass;
  private String toClass;
  private final Conversion conversion;

  public ClassMapping() {
    this.conversion = new Conversion();
  }

  public ClassTypeMapping toSettingsClassMapping() {
    return new ClassTypeMapping(fromClass, toClass, conversion.toTypeConversion());
  }

  void assertCompleteTypeConversion() {
    conversion.assertValidConfig(
        String.format("classTypeMapping with fromClass '%s' and toClass '%s'", fromClass, toClass));
  }
}
