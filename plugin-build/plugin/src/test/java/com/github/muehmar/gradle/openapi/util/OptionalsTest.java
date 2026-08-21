package com.github.muehmar.gradle.openapi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class OptionalsTest {

  @Test
  void combine_noArgumentsPresent_then_emptyResult() {
    final Optional<Integer> int1 = Optional.empty();
    final Optional<Integer> int2 = Optional.empty();

    final Optional<Integer> result =
        Optionals.combine(int1, int2, i -> i + 5, i -> i + 10, (i1, i2) -> i1 * i2);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void combine_firstArgumentPresent_then_firstFunctionApplied() {
    final Optional<Integer> int1 = Optional.of(5);
    final Optional<Integer> int2 = Optional.empty();

    final Optional<Integer> result =
        Optionals.combine(int1, int2, i -> i + 5, i -> i + 10, (i1, i2) -> i1 * i2);

    assertEquals(Optional.of(10), result);
  }

  @Test
  void combine_secondArgumentPresent_then_secondFunctionApplied() {
    final Optional<Integer> int1 = Optional.empty();
    final Optional<Integer> int2 = Optional.of(5);

    final Optional<Integer> result =
        Optionals.combine(int1, int2, i -> i + 5, i -> i + 10, (i1, i2) -> i1 * i2);

    assertEquals(Optional.of(15), result);
  }

  @Test
  void combine_bothArgumentsPresent_then_combineFunctionApplied() {
    final Optional<Integer> int1 = Optional.of(5);
    final Optional<Integer> int2 = Optional.of(5);

    final Optional<Integer> result =
        Optionals.combine(int1, int2, i -> i + 5, i -> i + 10, (i1, i2) -> i1 * i2);

    assertEquals(Optional.of(25), result);
  }
}
