package com.github.muehmar.gradle.openapi.issues.issue421;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * A container as additional-property value type is mapped to a dedicated pojo which is referenced
 * as object type, i.e. the value type itself is never a container. This asserts that the generated
 * code behaves correctly at runtime, in particular the conversion needed for the string-backed
 * representation of an enum nested within such a container.
 */
class Issue421Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_arrayOfEnumValueType_then_enumItemsConverted() throws Exception {
    final PaletteDto dto = MAPPER.readValue("{\"warm\":[\"red\",\"green\"]}", PaletteDto.class);

    assertEquals(
        Optional.of(Arrays.asList(ColorEnumDto.RED, ColorEnumDto.GREEN)),
        dto.getAdditionalProperty("warm").map(PalettePropertyDto::getItems));
  }

  @Test
  void serialize_when_arrayOfEnumValueType_then_enumItemsWrittenAsString() throws Exception {
    final PaletteDto dto =
        PaletteDto.builder()
            .addAdditionalProperty(
                "warm", PalettePropertyDto.fromItems(Arrays.asList(ColorEnumDto.RED)))
            .build();

    assertEquals("{\"warm\":[\"red\"]}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_arrayOfInlineEnumValueType_then_nestedEnumItemsConverted()
      throws Exception {
    final InlinePaletteDto dto = MAPPER.readValue("{\"axis\":[\"north\"]}", InlinePaletteDto.class);

    assertEquals(
        Optional.of(Collections.singletonList(InlinePalettePropertyDto.ValueEnum.NORTH)),
        dto.getAdditionalProperty("axis").map(InlinePalettePropertyDto::getItems));
  }

  @Test
  void deserialize_when_mapOfEnumValueType_then_enumValuesConverted() throws Exception {
    final InventoryDto dto =
        MAPPER.readValue("{\"store\":{\"apple\":\"sold-out\"}}", InventoryDto.class);

    assertEquals(
        Optional.of(InventoryPropertyDto.PropertyEnum.SOLD_OUT),
        dto.getAdditionalProperty("store")
            .flatMap(property -> property.getAdditionalProperty("apple")));
  }

  @Test
  void serialize_when_mapOfEnumValueType_then_enumValuesWrittenAsString() throws Exception {
    final InventoryDto dto =
        InventoryDto.builder()
            .addAdditionalProperty(
                "store",
                InventoryPropertyDto.builder()
                    .addAdditionalProperty("apple", InventoryPropertyDto.PropertyEnum.SOLD_OUT)
                    .build())
            .build();

    assertEquals("{\"store\":{\"apple\":\"sold-out\"}}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_nullableContainerValueTypeIsNull_then_tristateIsNull() throws Exception {
    final NullablePaletteDto dto = MAPPER.readValue("{\"warm\":null}", NullablePaletteDto.class);

    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("warm"));
  }

  @Test
  void deserialize_when_requiredAdditionalPropertyWithContainerValueType_then_valueConverted()
      throws Exception {
    final TicketDto dto = MAPPER.readValue("{\"colors\":[\"blue\"]}", TicketDto.class);

    assertEquals(
        Optional.of(Collections.singletonList(ColorEnumDto.BLUE)),
        dto.getAdditionalProperty("colors").map(TicketPropertyDto::getItems));

    final Set<ConstraintViolation<TicketDto>> violations = validate(dto);

    assertTrue(violations.isEmpty());
    assertTrue(dto.isValid());
  }

  @Test
  void deserialize_when_enumValueTypeWithoutContainer_then_enumConverted() throws Exception {
    final FlagsDto dto = MAPPER.readValue("{\"primary\":\"green\"}", FlagsDto.class);

    assertEquals(Optional.of(ColorEnumDto.GREEN), dto.getAdditionalProperty("primary"));
  }
}
