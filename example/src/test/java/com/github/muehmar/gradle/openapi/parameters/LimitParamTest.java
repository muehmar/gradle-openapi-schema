package com.github.muehmar.gradle.openapi.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import openapischema.example.api.parameters.model.parameter.LimitParam;
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
  @CsvSource({"-1000,false", "-1,false", "0,false", "1,true", "50,true", "51,false"})
  void testMatchesLimits(int value, boolean exceedLimits) {
    assertEquals(exceedLimits, LimitParam.matchesLimits(value));
  }
}
