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

class FloatValidationTest {
  @ParameterizedTest
  @ValueSource(floats = {200.25f, 255.67f, 300.499f})
  void validate_when_ok_then_noViolations(float value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setFloatValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(floats = {1, 200.2499f, 300.5f, 2000})
  void validate_when_exceedsRange_then_violation(float value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setFloatValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
