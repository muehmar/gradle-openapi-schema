package com.github.muehmar.gradle.openapi.issues.issue396;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * A {@code type: number} property without {@code format} used to map to {@code java.lang.Float}.
 * JSON numbers are double-precision, so a round-trip silently lost precision. The default is {@code
 * Double}, while an explicitly declared format is still honoured.
 */
class Issue396Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void roundTrip_when_formatLessNumberWithDoublePrecision_then_valuePreserved() throws Exception {
    final String json = "{\"price\":3.141592653589793}";

    final ProductDto dto = MAPPER.readValue(json, ProductDto.class);

    assertEquals(json, MAPPER.writeValueAsString(dto));
  }

  @Test
  void getPrice_when_noFormatDeclared_then_returnsDouble() throws Exception {
    assertEquals(Double.class, ProductDto.class.getMethod("getPrice").getReturnType());
  }

  @Test
  void getWeightOr_when_formatFloatDeclared_then_returnsFloat() throws Exception {
    assertEquals(
        Float.class, ProductDto.class.getMethod("getWeightOr", Float.class).getReturnType());
  }

  @Test
  void getVolumeOr_when_formatDoubleDeclared_then_returnsDouble() throws Exception {
    assertEquals(
        Double.class, ProductDto.class.getMethod("getVolumeOr", Double.class).getReturnType());
  }
}
