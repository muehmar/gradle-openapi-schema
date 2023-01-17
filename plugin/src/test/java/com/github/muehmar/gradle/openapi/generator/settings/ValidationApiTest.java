package com.github.muehmar.gradle.openapi.generator.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ValidationApiTest {
  @ParameterizedTest
  @MethodSource("validationApiInputs")
  void fromString_when_inputs_then_correctMapped(
      String input, ValidationApi expectedValidationApi) {
    Optional<ValidationApi> validationApi = ValidationApi.fromString(input);

    assertEquals(Optional.of(expectedValidationApi), validationApi);
  }

  public static Stream<Arguments> validationApiInputs() {
    return Stream.of(
        arguments("jakarta-2", ValidationApi.JAKARTA_2_0),
        arguments("jakarta-2.0", ValidationApi.JAKARTA_2_0),
        arguments("jakarta-3", ValidationApi.JAKARTA_3_0),
        arguments("jakarta-3.0", ValidationApi.JAKARTA_3_0));
  }
}
