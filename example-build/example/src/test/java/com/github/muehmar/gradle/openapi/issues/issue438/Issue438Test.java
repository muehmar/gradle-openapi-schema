package com.github.muehmar.gradle.openapi.issues.issue438;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

/**
 * The names generated for a property must not collide with the names generated for one of its
 * siblings. That the dtos of this issue compile at all is the main assertion; the tests below make
 * sure that renaming the json anchors did not break the (de)serialization they anchor.
 */
class Issue438Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_siblingNamedAfterTheJsonAnchor_then_bothPropertiesMapped()
      throws Exception {
    final JsonAnchorCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":\"aName\",\"nameJson\":\"aNameJson\"}", JsonAnchorCollisionDto.class);

    assertEquals("aName", dto.getName());
    assertEquals("aNameJson", dto.getNameJson());
  }

  @Test
  void serialize_when_siblingNamedAfterTheJsonAnchor_then_bothPropertiesWritten() throws Exception {
    final JsonAnchorCollisionDto dto =
        JsonAnchorCollisionDto.builder().setName("aName").setNameJson("aNameJson").build();

    assertEquals("{\"name\":\"aName\",\"nameJson\":\"aNameJson\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_anchorRenamingChains_then_allPropertiesMapped() throws Exception {
    final JsonAnchorChainCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":\"a\",\"nameJson\":\"b\",\"nameJsonJson\":\"c\"}",
            JsonAnchorChainCollisionDto.class);

    assertEquals("a", dto.getName());
    assertEquals("b", dto.getNameJson());
    assertEquals("c", dto.getNameJsonJson());
  }

  @Test
  void serialize_when_anchorRenamingChains_then_allPropertiesWritten() throws Exception {
    final JsonAnchorChainCollisionDto dto =
        JsonAnchorChainCollisionDto.builder()
            .setName("a")
            .setNameJson("b")
            .setNameJsonJson("c")
            .build();

    assertEquals(
        "{\"name\":\"a\",\"nameJson\":\"b\",\"nameJsonJson\":\"c\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_renamedAnchorOfNullableProperty_then_nullMappedToTristateNull()
      throws Exception {
    final JsonAnchorCollisionOptionalDto dto =
        MAPPER.readValue(
            "{\"name\":null,\"nameJson\":\"aNameJson\"}", JsonAnchorCollisionOptionalDto.class);

    assertEquals(Tristate.ofNull(), dto.getNameTristate());
    assertEquals("aNameJson", dto.getNameJson());
  }

  @Test
  void serialize_when_renamedAnchorOfNullableProperty_then_nullWritten() throws Exception {
    final JsonAnchorCollisionOptionalDto dto =
        JsonAnchorCollisionOptionalDto.builder()
            .setNameJson("aNameJson")
            .andAllOptionals()
            .setName(Tristate.ofNull())
            .build();

    assertEquals("{\"name\":null,\"nameJson\":\"aNameJson\"}", MAPPER.writeValueAsString(dto));
  }
}
