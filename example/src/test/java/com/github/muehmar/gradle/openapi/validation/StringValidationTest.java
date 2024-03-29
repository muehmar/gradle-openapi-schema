package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringValidationTest {
  @ParameterizedTest
  @ValueSource(strings = {"A0", "MM12345678", "abcd"})
  void validate_when_ok_then_noViolations(String value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setStringValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "A", "!ABC!", "AB123456789"})
  void validate_when_wrongSizeOrDoesNotMatchPattern_then_violation(String value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setStringValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }
}
