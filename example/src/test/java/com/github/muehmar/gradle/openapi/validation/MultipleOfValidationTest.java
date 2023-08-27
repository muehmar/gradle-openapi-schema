package com.github.muehmar.gradle.openapi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import openapischema.example.api.validation.model.MultipleOfObjectDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MultipleOfValidationTest {
  @ParameterizedTest
  @MethodSource("notMultipleOfInts")
  void validate_when_intNotMultipleOf_then_violation(int value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setIntValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "intValue is not a multiple of 11", violations.stream().findFirst().get().getMessage());
  }

  public static Stream<Arguments> notMultipleOfInts() {
    return IntStream.range(1, 20).boxed().filter(i -> i != 11).map(Arguments::arguments);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 11, 22, 33})
  void validate_when_intMultipleOf_then_noViolation(int value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setIntValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }

  @ParameterizedTest
  @MethodSource("notMultipleOfLongs")
  void validate_when_longNotMultipleOf_then_violation(long value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setLongValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "longValue is not a multiple of 22", violations.stream().findFirst().get().getMessage());
  }

  public static Stream<Arguments> notMultipleOfLongs() {
    return IntStream.range(1, 30)
        .mapToObj(i -> (long) i)
        .filter(i -> i != 22)
        .map(Arguments::arguments);
  }

  @ParameterizedTest
  @ValueSource(longs = {0, 22, 44, 66})
  void validate_when_longMultipleOf_then_noViolation(long value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setLongValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }

  @ParameterizedTest
  @MethodSource("notMultipleOfFloats")
  void validate_when_doubleNotMultipleOf_then_violation(float value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setFloatValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "floatValue is not a multiple of 2.25", violations.stream().findFirst().get().getMessage());
  }

  public static Stream<Arguments> notMultipleOfFloats() {
    return IntStream.range(1, 20)
        .mapToObj(i -> ((float) i) / 4)
        .filter(f -> f != 2.25)
        .filter(f -> f != 4.5)
        .map(Arguments::arguments);
  }

  @ParameterizedTest
  @ValueSource(floats = {0, 2.25f, 4.5f, 6.75f, 9})
  void validate_when_floatMultipleOf_then_noViolation(float value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setFloatValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }

  @ParameterizedTest
  @MethodSource("notMultipleOfDoubles")
  void validate_when_doubleNotMultipleOf_then_violation(double value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setDoubleValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "doubleValue is not a multiple of 5.5", violations.stream().findFirst().get().getMessage());
  }

  public static Stream<Arguments> notMultipleOfDoubles() {
    return IntStream.range(1, 20)
        .mapToObj(i -> ((double) i) / 2)
        .filter(d -> d != 5.5)
        .map(Arguments::arguments);
  }

  @ParameterizedTest
  @ValueSource(doubles = {0, 5.5, 11, 16.5, 22})
  void validate_when_doubleMultipleOf_then_noViolation(double value) {
    final MultipleOfObjectDto dto =
        MultipleOfObjectDto.newBuilder().andOptionals().setDoubleValue(value).build();

    final Set<ConstraintViolation<MultipleOfObjectDto>> violations = ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }
}
