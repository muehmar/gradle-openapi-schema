package com.github.muehmar.gradle.openapi.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.parameters.parameter.ThemeParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ThemeParamTest {
  @Test
  void testConstants() {
    assertEquals(2, ThemeParam.MIN_LENGTH);
    assertEquals(10, ThemeParam.MAX_LENGTH);
    assertEquals("theme0", ThemeParam.DEFAULT);
    assertEquals("[a-z]+\\d", ThemeParam.PATTERN_STR);
  }

  @ParameterizedTest
  @CsvSource({"'',false", "t0,true", "theme0,true", "themetheme,true", "themetheme0,false"})
  void testMatchesLimits(String value, boolean exceedLimits) {
    assertEquals(exceedLimits, ThemeParam.matchesLimits(value));
  }

  @ParameterizedTest
  @CsvSource({"'',false", "t,false", "0,false", "t0,true", "theme0,true", "theme,false"})
  void testMatchesPattern(String value, boolean matchesPattern) {
    assertEquals(matchesPattern, ThemeParam.matchesPattern(value));
  }
}
