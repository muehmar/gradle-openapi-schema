package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class NoAdditionalPropertiesValidationMethodGenerator {
  private NoAdditionalPropertiesValidationMethodGenerator() {}

  public static Generator<JavaAdditionalProperties, PojoSettings>
      noAdditionalPropertiesValidationMethodGenerator() {
    return ValidationGenerator.<JavaAdditionalProperties>assertTrue(
            ignore -> "No additional properties allowed")
        .append(method())
        .filter(additionalProperties -> not(additionalProperties.isAllowed()))
        .filter(Filters.isValidationEnabled());
  }

  private static MethodGen<JavaAdditionalProperties, PojoSettings> method() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("hasNoAdditionalProperties")
        .noArguments()
        .content(
            String.format("return %s.size() == 0;", JavaAdditionalProperties.getPropertyName()))
        .build();
  }
}
