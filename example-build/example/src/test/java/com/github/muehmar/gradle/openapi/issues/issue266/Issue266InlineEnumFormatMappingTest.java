package com.github.muehmar.gradle.openapi.issues.issue266;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266: an INLINE enum mapped to a custom type with a conversion (via its format). In contrast
 * to the {@code dtoMapping} of a referenced enum, the enum itself stays inline and is generated as
 * nested class of the referencing dto. The api type of the member therefore combines the user
 * conversion with the plugin conversion of the enum, which has to be rebuilt for the enum re-nested
 * into the dto.
 */
class Issue266InlineEnumFormatMappingTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void builder_when_colorSet_then_returnsCustomColor() {
    final InlineColor color = InlineColor.fromEnum(Palette2Dto.ColorEnum.GREEN);

    final Palette2Dto palette = Palette2Dto.fullPalette2DtoBuilder().setColor(color).build();

    assertEquals(color, palette.getColor());
  }

  @Test
  void serialize_when_paletteWithCustomColor_then_jsonContainsEnumValue() throws Exception {
    final Palette2Dto palette =
        Palette2Dto.fullPalette2DtoBuilder()
            .setColor(InlineColor.fromEnum(Palette2Dto.ColorEnum.RED))
            .build();

    assertEquals("{\"color\":\"red\"}", MAPPER.writeValueAsString(palette));
  }

  @Test
  void deserialize_when_jsonWithEnumValue_then_returnsCustomColor() throws Exception {
    final Palette2Dto palette = MAPPER.readValue("{\"color\":\"blue\"}", Palette2Dto.class);

    assertEquals(InlineColor.fromEnum(Palette2Dto.ColorEnum.BLUE), palette.getColor());

    final Set<ConstraintViolation<Palette2Dto>> violations = validate(palette);
    assertEquals(0, violations.size());
    assertTrue(palette.isValid());
  }

  @Test
  void deserialize_when_enumValueOutOfRange_then_violationAndGetterThrows() throws Exception {
    // The out-of-range contract also holds for a mapped inline enum: no exception during
    // deserialisation, a violation on the Raw validation getter and the getter throws, as
    // fromValue runs before the conversion to the custom type.
    final Palette2Dto palette = MAPPER.readValue("{\"color\":\"purple\"}", Palette2Dto.class);

    final Set<ConstraintViolation<Palette2Dto>> violations = validate(palette);

    assertEquals(
        Collections.singletonList("colorRaw -> must match \"red|green|blue\""),
        formatViolations(violations));
    assertFalse(palette.isValid());
    assertThrows(IllegalArgumentException.class, palette::getColor);
  }
}
