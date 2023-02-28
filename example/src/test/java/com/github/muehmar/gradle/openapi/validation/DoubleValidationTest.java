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

class DoubleValidationTest {
  @ParameterizedTest
  @ValueSource(doubles = {5.1, 7.0, 100.49999})
  void validate_when_ok_then_noViolations(double value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().setDoubleValue(value).andAllOptionals().build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1, 5.0999, 100.5, 200})
  void validate_when_exceedsRange_then_violation(double value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().setDoubleValue(value).andAllOptionals().build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations =
        validator.validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
