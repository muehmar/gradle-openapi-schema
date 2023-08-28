package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.validation.model.AllValueObjectDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LongValidationTest {
  @ParameterizedTest
  @ValueSource(longs = {-122, -1, 0, 1, 249})
  void validate_when_ok_then_noViolations(long value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setLongValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(longs = {-1000, -123, 250, 5000})
  void validate_when_exceedsRange_then_violation(long value) {
    final AllValueObjectDto dto =
        AllValueObjectDto.builder().andOptionals().setLongValue(value).build();

    final Set<ConstraintViolation<AllValueObjectDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
