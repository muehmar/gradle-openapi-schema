package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.validation.model.IntegerArrayDto;
import java.util.ArrayList;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArrayValidationTest {
  @ParameterizedTest
  @ValueSource(ints = {3, 4, 5})
  void validate_when_validSize_then_noViolations(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerArrayDto dto = IntegerArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<IntegerArrayDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 6, 7})
  void validate_when_invalidSize_then_violation(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerArrayDto dto = IntegerArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<IntegerArrayDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
