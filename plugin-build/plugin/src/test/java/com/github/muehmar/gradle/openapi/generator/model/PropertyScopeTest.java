package com.github.muehmar.gradle.openapi.generator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PropertyScopeTest {

  @ParameterizedTest
  @CsvSource({"WRITE_ONLY,false", "READ_ONLY,true", "DEFAULT,true"})
  void isUsedInResponse_when_called_then_correctFlag(PropertyScope scope, boolean expectedFlag) {
    assertEquals(expectedFlag, scope.isUsedInResponse());
  }

  @ParameterizedTest
  @CsvSource({"WRITE_ONLY,true", "READ_ONLY,false", "DEFAULT,true"})
  void isUsedInRequest_when_called_then_correctFlag(PropertyScope scope, boolean expectedFlag) {
    assertEquals(expectedFlag, scope.isUsedInRequest());
  }
}
