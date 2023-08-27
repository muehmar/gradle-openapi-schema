package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.validation.model.IntegerMemberArrayDto;
import openapischema.example.api.validation.model.IntegerPojoArrayDto;
import openapischema.example.api.validation.model.LongMemberArrayDto;
import openapischema.example.api.validation.model.LongPojoArrayDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArrayValidationTest {
  @ParameterizedTest
  @ValueSource(ints = {3, 4, 5})
  void validate_when_memberArrayAndValidSize_then_noViolations(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerMemberArrayDto dto =
        IntegerMemberArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<IntegerMemberArrayDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 6, 7})
  void validate_when_memberArrayAndInvalidSize_then_violation(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerMemberArrayDto dto =
        IntegerMemberArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<IntegerMemberArrayDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "size must be between 3 and 5",
        constraintViolations.stream().findFirst().get().getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {3, 4, 5})
  void validate_when_pojoArrayAndValidSize_then_noViolations(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerPojoArrayDto dto = new IntegerPojoArrayDto(numbers);

    final Set<ConstraintViolation<IntegerPojoArrayDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 6, 7})
  void validate_when_pojoArrayAndInvalidSize_then_violation(int size) {
    final ArrayList<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      numbers.add(i);
    }

    final IntegerPojoArrayDto dto = new IntegerPojoArrayDto(numbers);

    final Set<ConstraintViolation<IntegerPojoArrayDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "size must be between 3 and 5",
        constraintViolations.stream().findFirst().get().getMessage());
  }

  @Test
  void validate_when_singleValueInArrayViolatesMaximum_then_violation() {
    final ArrayList<Integer> integers = new ArrayList<>(Arrays.asList(1, 2, 3, 150));
    final IntegerPojoArrayDto dto = new IntegerPojoArrayDto(integers);

    final Set<ConstraintViolation<IntegerPojoArrayDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "must be less than or equal to 100", violations.stream().findFirst().get().getMessage());
  }

  @Test
  void validate_when_memberArrayAndUniqueItems_then_noViolation() {
    final ArrayList<Long> numbers = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));

    final LongMemberArrayDto dto = LongMemberArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<LongMemberArrayDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_memberArrayAndNotUniqueItems_then_violation() {
    final ArrayList<Long> numbers = new ArrayList<>(Arrays.asList(1L, 2L, 2L, 4L, 5L));

    final LongMemberArrayDto dto = LongMemberArrayDto.newBuilder().setNumbers(numbers).build();

    final Set<ConstraintViolation<LongMemberArrayDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "numbers does not contain unique items",
        constraintViolations.stream().findFirst().get().getMessage());
  }

  @Test
  void validate_when_pojoArrayAndUniqueItems_then_noViolation() {
    final ArrayList<Long> numbers = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));

    final LongPojoArrayDto dto = new LongPojoArrayDto(numbers);

    final Set<ConstraintViolation<LongPojoArrayDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_pojoArrayAndNotUniqueItems_then_violation() {
    final ArrayList<Long> numbers = new ArrayList<>(Arrays.asList(1L, 2L, 2L, 4L, 5L));

    final LongPojoArrayDto dto = new LongPojoArrayDto(numbers);

    final Set<ConstraintViolation<LongPojoArrayDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "value does not contain unique items",
        constraintViolations.stream().findFirst().get().getMessage());
  }
}
