package com.github.muehmar.gradle.openapi.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.parameters.model.parameter.OffsetParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OffsetParamTest {
  @Test
  void testConstants() {
    assertEquals(0, OffsetParam.MIN);
  }

  @ParameterizedTest
  @CsvSource({"-1000,true", "-1,true", "0,false", "100,false", "10000,false"})
  void testExceedLimits(int value, boolean exceedLimits) {
    assertEquals(exceedLimits, OffsetParam.exceedLimits(value));
  }
}
