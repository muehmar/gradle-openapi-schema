package com.github.muehmar.gradle.openapi.warnings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import org.junit.jupiter.api.Test;

class WarningTest {

  @Test
  void unsupportedValidation_when_called_then_correctTypeAndMessage() {
    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(
            JavaPojoNames.invoiceName(), JavaName.fromString("member"));

    final Warning warning =
        Warning.unsupportedValidation(
            propertyInfoName, JavaTypes.stringType(), ConstraintType.MULTIPLE_OF);

    assertEquals(WarningType.UNSUPPORTED_VALIDATION, warning.getType());
    assertEquals(
        "The type java.lang.String of property InvoiceDto.member can not be validated against the constraint 'multiple_of', i.e. no annotations or code is generated for validation.",
        warning.getMessage());
  }
}
