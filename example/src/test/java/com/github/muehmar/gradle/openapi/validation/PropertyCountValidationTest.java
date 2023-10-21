package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class PropertyCountValidationTest {
  @Test
  void validate_when_oneProperties_then_violation() {
    final NumbersObjectDto dto =
        NumbersObjectDto.builder().andOptionals().setDoubleValue(4.0).build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_twoProperties_then_noViolations() {
    final NumbersObjectDto dto =
        NumbersObjectDto.builder().andOptionals().setDoubleValue(4.0).setFloatValue(4.0f).build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_threeProperties_then_noViolations() {
    final NumbersObjectDto dto =
        NumbersObjectDto.builder()
            .andOptionals()
            .setDoubleValue(4.0)
            .setFloatValue(4.0f)
            .setIntValue(46)
            .build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_fourProperties_then_violation() {
    final NumbersObjectDto dto =
        NumbersObjectDto.builder()
            .andOptionals()
            .setDoubleValue(4.0)
            .setFloatValue(4.0f)
            .setIntValue(46)
            .setLongValue(1736L)
            .build();

    final Set<ConstraintViolation<NumbersObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertFalse(dto.isValid());
  }
}
