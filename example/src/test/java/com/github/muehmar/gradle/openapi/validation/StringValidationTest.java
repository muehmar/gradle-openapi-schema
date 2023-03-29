package com.github.muehmar.gradle.openapi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.validation.model.AllValueObjectDto;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringValidationTest {
  @ParameterizedTest
  @ValueSource(strings = {"A0", "MM12345678", "abcd"})
  void validate_when_ok_then_noViolations(String value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setStringValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "A", "!ABC!", "AB123456789"})
  void validate_when_wrongSizeOrDoesNotMatchPattern_then_violation(String value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setStringValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
