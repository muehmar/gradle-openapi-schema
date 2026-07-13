package com.github.muehmar.gradle.openapi.issues.issue396;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * A {@code type: number} property without {@code format} maps to {@code java.lang.Float}. JSON
 * numbers are double-precision, so a round-trip silently loses precision. The default should be
 * {@code Double} (or configurable).
 */
public class Issue396Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void roundTrip_when_formatLessNumberWithDoublePrecision_then_valuePreserved() throws Exception {
    final String json = "{\"price\":3.141592653589793}";
    final ProductDto dto = MAPPER.readValue(json, ProductDto.class);

    assertEquals(json, MAPPER.writeValueAsString(dto));
  }
}
