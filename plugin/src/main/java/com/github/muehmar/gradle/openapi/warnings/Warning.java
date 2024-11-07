package com.github.muehmar.gradle.openapi.warnings;

import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.DtoMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import lombok.Value;

/** Warning which may occur during the generation of the code for the schemas. */
@Value
public class Warning {
  WarningType type;
  String message;

  public static Warning unsupportedValidation(
      PropertyInfoName propertyInfoName, JavaType javaType, ConstraintType constraintType) {
    final String message =
        String.format(
            "The type %s of property %s can not be validated against the constraint '%s', i.e. no annotations or code is generated for validation.",
            javaType.getQualifiedClassName().asString(),
            propertyInfoName,
            constraintType.name().toLowerCase());
    return new Warning(WarningType.UNSUPPORTED_VALIDATION, message);
  }

  public static Warning missingMappingConversion(ClassTypeMapping classTypeMapping) {
    final String message =
        String.format(
            "ClassTypeMapping for class %s has no conversion defined.",
            classTypeMapping.getFromClass());
    return new Warning(WarningType.MISSING_MAPPING_CONVERSION, message);
  }

  public static Warning missingMappingConversion(FormatTypeMapping formatTypeMapping) {
    final String message =
        String.format(
            "FormatTypeMapping for format %s has no conversion defined.",
            formatTypeMapping.getFormatType());
    return new Warning(WarningType.MISSING_MAPPING_CONVERSION, message);
  }

  public static Warning missingMappingConversion(DtoMapping dtoMapping) {
    final String message =
        String.format("DtoMapping for DTO %s has no conversion defined.", dtoMapping.getDtoName());
    return new Warning(WarningType.MISSING_MAPPING_CONVERSION, message);
  }
}
