package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class NoAdditionalPropertiesValidationMethodGenerator {
  private NoAdditionalPropertiesValidationMethodGenerator() {}

  public static Generator<JavaAdditionalProperties, PojoSettings>
      noAdditionalPropertiesValidationMethodGenerator() {
    return ValidationAnnotationGenerator.<JavaAdditionalProperties>assertTrue(
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
        .doesNotThrow()
        .content(constant("return %s.size() == 0;", additionalPropertiesName()))
        .build();
  }
}
