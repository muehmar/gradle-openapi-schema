package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WarningsConfigTest {

  @ParameterizedTest
  @MethodSource("commonAndExplicitFlags")
  void withCommonWarnings_when_disableWarningsFlag_then_matchExpected(
      Boolean common, Boolean explicit, Boolean expected) {
    final WarningsConfig commonWarnings = new WarningsConfig(common, false, false, false);

    final WarningsConfig explicitWarnings = new WarningsConfig(explicit, false, false, false);

    final WarningsConfig resultingWarnings = explicitWarnings.withCommonWarnings(commonWarnings);

    assertEquals(expected, resultingWarnings.getDisableWarnings());
  }

  @ParameterizedTest
  @MethodSource("commonAndExplicitFlags")
  void withCommonWarnings_when_failOnWarningsFlag_then_matchExpected(
      Boolean common, Boolean explicit, Boolean expected) {
    final WarningsConfig commonWarnings = new WarningsConfig(false, common, false, false);

    final WarningsConfig explicitWarnings = new WarningsConfig(false, explicit, false, false);

    final WarningsConfig resultingWarnings = explicitWarnings.withCommonWarnings(commonWarnings);

    assertEquals(expected, resultingWarnings.getFailOnWarnings());
  }

  @ParameterizedTest
  @MethodSource("commonAndExplicitFlags")
  void withCommonWarnings_when_failOnUnsupportedValidationFlag_then_matchExpected(
      Boolean common, Boolean explicit, Boolean expected) {
    final WarningsConfig commonWarnings = new WarningsConfig(false, false, common, false);

    final WarningsConfig explicitWarnings = new WarningsConfig(false, false, explicit, false);

    final WarningsConfig resultingWarnings = explicitWarnings.withCommonWarnings(commonWarnings);

    assertEquals(expected, resultingWarnings.getFailOnUnsupportedValidation());
  }

  @ParameterizedTest
  @MethodSource("commonAndExplicitFlags")
  void withCommonWarnings_when_failOnMissingMappingConversion_then_matchExpected(
      Boolean common, Boolean explicit, Boolean expected) {
    final WarningsConfig commonWarnings = new WarningsConfig(false, false, false, common);

    final WarningsConfig explicitWarnings = new WarningsConfig(false, false, false, explicit);

    final WarningsConfig resultingWarnings = explicitWarnings.withCommonWarnings(commonWarnings);

    assertEquals(expected, resultingWarnings.getFailOnMissingMappingConversion());
  }

  private static Stream<Arguments> commonAndExplicitFlags() {
    return Stream.of(
        arguments(null, false, false),
        arguments(null, true, true),
        arguments(null, null, false),
        arguments(false, false, false),
        arguments(false, true, true),
        arguments(false, null, false),
        arguments(true, false, false),
        arguments(true, true, true),
        arguments(true, null, true));
  }
}
