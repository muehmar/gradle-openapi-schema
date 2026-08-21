package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DoubleValidationTest {
  @ParameterizedTest
  @ValueSource(doubles = {5.1, 7.0, 100.49999})
  void validate_when_ok_then_noViolations(double value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setDoubleValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1, 5.0999, 100.5, 200})
  void validate_when_exceedsRange_then_violation(double value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setDoubleValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }
}
