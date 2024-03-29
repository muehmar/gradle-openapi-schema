package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class MapTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  private static final String ROOT_MAP_JSON =
      "{\"prop2\":{\"description\":\"description2\",\"name\":\"name2\"},\"prop1\":{\"name\":\"name1\"}}";
  private static final String INLINE_MAP_JSON =
      "{\"map\":{\"prop2\":{\"description\":\"description2\",\"name\":\"name2\"},\"prop1\":{\"name\":\"name1\"}}}";
  private static final RootMapSchemaDto ROOT_MAP_DTO;
  private static final InlinedMapSchemaDto INLINE_MAP_DTO;

  static {
    final HashMap<String, RootMapSchemaPropertyDto> map = new HashMap<>();
    map.put("prop1", RootMapSchemaPropertyDto.builder().setName("name1").build());
    map.put(
        "prop2",
        RootMapSchemaPropertyDto.builder()
            .setName("name2")
            .andAllOptionals()
            .setDescription("description2")
            .build());
    ROOT_MAP_DTO = RootMapSchemaDto.fromProperties(map);
  }

  static {
    final HashMap<String, InlinedMapSchemaMapDto> map = new HashMap<>();
    map.put("prop1", InlinedMapSchemaMapDto.builder().setName("name1").build());
    map.put(
        "prop2",
        InlinedMapSchemaMapDto.builder()
            .setName("name2")
            .andAllOptionals()
            .setDescription("description2")
            .build());
    INLINE_MAP_DTO = InlinedMapSchemaDto.builder().andAllOptionals().setMap(map).build();
  }

  @Test
  void writeValueAsString_when_rootMap_then_correctJson() throws JsonProcessingException {
    assertEquals(ROOT_MAP_JSON, MAPPER.writeValueAsString(ROOT_MAP_DTO));
  }

  @Test
  void readValue_when_rootMap_then_correctDto() throws JsonProcessingException {
    assertEquals(ROOT_MAP_DTO, MAPPER.readValue(ROOT_MAP_JSON, RootMapSchemaDto.class));
  }

  @Test
  void writeValueAsString_when_inlineMap_then_correctJson() throws JsonProcessingException {
    assertEquals(INLINE_MAP_JSON, MAPPER.writeValueAsString(INLINE_MAP_DTO));
  }

  @Test
  void readValue_when_inlineMap_then_correctDto() throws JsonProcessingException {
    assertEquals(INLINE_MAP_DTO, MAPPER.readValue(INLINE_MAP_JSON, InlinedMapSchemaDto.class));
  }
}
