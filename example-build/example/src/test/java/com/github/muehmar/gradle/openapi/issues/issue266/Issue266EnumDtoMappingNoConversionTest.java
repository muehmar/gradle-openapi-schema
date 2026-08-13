package com.github.muehmar.gradle.openapi.issues.issue266;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Issue 266 (follow-up): a {@code dtoMapping} WITHOUT conversion on a referenced ({@code $ref})
 * enum schema must replace the generated enum entirely with the custom type. Previously the
 * String-typed enum api conversions ({@code fromValue}/{@code getValue}) were kept against the
 * custom-typed member, producing uncompilable generated code — this test is primarily compile-level
 * regression coverage.
 */
class Issue266EnumDtoMappingNoConversionTest {

  @Test
  void builder_when_colorSet_then_returnsCustomColor() {
    final ColorNoConversion color = new ColorNoConversion("green");

    final NoConversionPaletteDto palette =
        NoConversionPaletteDto.fullNoConversionPaletteDtoBuilder()
            .setName("warm")
            .setColor(color)
            .build();

    assertEquals(color, palette.getColor());
  }
}
