package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.validation.model.AllValueObjectDto;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class EmailValidationTest {
  @Test
  void validate_when_ok_then_noViolations() {
    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setEmail("muehmar@github.com").build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_invalidEmail_then_violation() {
    final AllValueObjectDto dto =
        AllValueObjectDto.newBuilder().andOptionals().setEmail("muehmar").build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
