package com.github.muehmar.gradle.openapi.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.parameters.model.parameter.LimitParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LimitParamTest {
  @Test
  void testConstants() {
    assertEquals(1, LimitParam.MIN);
    assertEquals(50, LimitParam.MAX);
    assertEquals(20, LimitParam.DEFAULT);
    assertEquals("20", LimitParam.DEFAULT_STR);
  }

  @ParameterizedTest
  @CsvSource({"-1000,true", "-1,true", "0,true", "1,false", "50,false", "51,true"})
  void testExceedLimits(int value, boolean exceedLimits) {
    assertEquals(exceedLimits, LimitParam.exceedLimits(value));
  }
}
