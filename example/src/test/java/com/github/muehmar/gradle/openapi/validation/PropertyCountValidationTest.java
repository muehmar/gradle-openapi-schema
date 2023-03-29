package com.github.muehmar.gradle.openapi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.validation.model.NumbersObjectDto;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class PropertyCountValidationTest {
  @Test
  void validate_when_oneProperties_then_violation() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final NumbersObjectDto dto =
        NumbersObjectDto.newBuilder().andOptionals().setDoubleValue(4.0).build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validator.validate(dto);

    assertEquals(1, constraintViolations.size());
  }

  @Test
  void validate_when_twoProperties_then_noViolations() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final NumbersObjectDto dto =
        NumbersObjectDto.newBuilder()
            .andOptionals()
            .setDoubleValue(4.0)
            .setFloatValue(4.0f)
            .build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validator.validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_threeProperties_then_noViolations() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final NumbersObjectDto dto =
        NumbersObjectDto.newBuilder()
            .andOptionals()
            .setDoubleValue(4.0)
            .setFloatValue(4.0f)
            .setIntValue(46)
            .build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validator.validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_fourProperties_then_violation() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final NumbersObjectDto dto =
        NumbersObjectDto.newBuilder()
            .andOptionals()
            .setDoubleValue(4.0)
            .setFloatValue(4.0f)
            .setIntValue(46)
            .setLongValue(1736L)
            .build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validator.validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
