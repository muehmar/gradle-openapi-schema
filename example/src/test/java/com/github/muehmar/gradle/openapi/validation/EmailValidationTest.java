package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class EmailValidationTest {
  @Test
  void validate_when_ok_then_noViolations() {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setEmail("muehmar@github.com").build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_invalidEmail_then_violation() {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setEmail("muehmar").build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }
}
