package com.github.muehmar.gradle.openapi.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.parameters.parameter.VoltageParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VoltageParamTest {
  @Test
  void testConstants() {
    assertEquals(3.8f, VoltageParam.MIN);
    assertTrue(VoltageParam.EXCLUSIVE_MIN);
    assertEquals(5.5f, VoltageParam.MAX);
    assertFalse(VoltageParam.EXCLUSIVE_MAX);
    assertEquals(5.15f, VoltageParam.DEFAULT);
    assertEquals("5.15", VoltageParam.DEFAULT_STR);
  }

  @ParameterizedTest
  @CsvSource({"3,false", "3.8,false", "3.81,true", "5.5,true", "5.51,false"})
  void testMatchesLimits(float value, boolean exceedLimits) {
    assertEquals(exceedLimits, VoltageParam.matchesLimits(value));
  }
}
