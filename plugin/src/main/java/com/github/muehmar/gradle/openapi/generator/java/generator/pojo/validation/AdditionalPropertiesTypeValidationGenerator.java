package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.assertTrue;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AdditionalPropertiesTypeValidationGenerator {
  private AdditionalPropertiesTypeValidationGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings>
      additionalPropertiesTypeValidationGenerator() {
    return annotatedCorrectTypeMethod()
        .filter(JavaAdditionalProperties::isAllowed)
        .filter(JavaAdditionalProperties::isNotValueAnyType)
        .contraMap(JavaObjectPojo::getAdditionalProperties);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> annotatedCorrectTypeMethod() {
    return JacksonAnnotationGenerator.<JavaAdditionalProperties>jsonIgnore()
        .append(
            assertTrue(
                props ->
                    String.format(
                        "Not all additional properties are instances of %s",
                        props.getType().getQualifiedClassName().getClassName())))
        .append(correctTypeMethod());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> correctTypeMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(SettingsFunctions::validationMethodModifiers)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isAllAdditionalPropertiesHaveCorrectType")
        .noArguments()
        .doesNotThrow()
        .content(
            constant(
                String.format(
                    "return getAdditionalProperties().size() == %s.size();",
                    additionalPropertiesName())))
        .build();
  }
}
