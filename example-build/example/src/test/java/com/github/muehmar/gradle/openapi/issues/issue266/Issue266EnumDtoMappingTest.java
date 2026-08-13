package com.github.muehmar.gradle.openapi.issues.issue266;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266 (follow-up): a {@code dtoMapping} configured on a referenced ({@code $ref}) enum schema
 * must be applied. A referenced enum is generated as its own DTO ({@code MappedColorEnumDto}) and
 * is modelled by a top-level {@code JavaEnumType} (see {@code JavaEnumType.wrapAsObjectType}),
 * which looked up the dto-mappings by the internal {@code String} class name instead of the enum's
 * class name and therefore silently dropped the mapping.
 *
 * <p>When the mapping is applied, the {@code color} property of {@code MappedPaletteDto} is the
 * custom {@link Color} type rather than the generated {@code MappedColorEnumDto}.
 */
class Issue266EnumDtoMappingTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void builder_when_colorSet_then_returnsCustomColor() {
    final Color color = Color.fromDto(MappedColorEnumDto.GREEN);

    final MappedPaletteDto palette =
        MappedPaletteDto.fullMappedPaletteDtoBuilder().setName("warm").setColor(color).build();

    assertEquals(color, palette.getColor());
  }

  @Test
  void serialize_when_paletteWithCustomColor_then_jsonContainsEnumValue() throws Exception {
    final MappedPaletteDto palette =
        MappedPaletteDto.fullMappedPaletteDtoBuilder()
            .setName("warm")
            .setColor(Color.fromDto(MappedColorEnumDto.RED))
            .build();

    final String json = MAPPER.writeValueAsString(palette);

    // MapperFactory enables SORT_PROPERTIES_ALPHABETICALLY, so properties are emitted sorted.
    assertEquals("{\"color\":\"red\",\"name\":\"warm\"}", json);
  }

  @Test
  void deserialize_when_jsonWithEnumValue_then_returnsCustomColor() throws Exception {
    final String json = "{\"name\":\"cool\",\"color\":\"blue\"}";

    final MappedPaletteDto palette = MAPPER.readValue(json, MappedPaletteDto.class);

    assertEquals("cool", palette.getName());
    assertEquals(Color.fromDto(MappedColorEnumDto.BLUE), palette.getColor());
  }

  @Test
  void deserialize_when_enumValueOutOfRange_then_violationAndGetterThrows() throws Exception {
    // The out-of-range contract also holds for a dto-mapped enum: no exception during
    // deserialisation, a violation on the Raw validation getter, the raw value round-trips and
    // the getter throws (fromValue runs before the conversion to the custom type).
    final MappedPaletteDto palette =
        MAPPER.readValue("{\"name\":\"warm\",\"color\":\"purple\"}", MappedPaletteDto.class);

    final Set<ConstraintViolation<MappedPaletteDto>> violations = validate(palette);

    assertEquals(
        Collections.singletonList("colorRaw -> must match \"red|green|blue\""),
        formatViolations(violations));
    assertEquals("{\"color\":\"purple\",\"name\":\"warm\"}", MAPPER.writeValueAsString(palette));
    assertThrows(IllegalArgumentException.class, palette::getColor);
  }
}
