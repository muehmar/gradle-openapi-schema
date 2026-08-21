package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntValidationTest {
  @ParameterizedTest
  @ValueSource(ints = {-5, -1, 0, 1, 22})
  void validate_when_ok_then_noViolations(int value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setIntValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1000, -6, 23, 5000})
  void validate_when_exceedsRange_then_violation(int value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setIntValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }
}
