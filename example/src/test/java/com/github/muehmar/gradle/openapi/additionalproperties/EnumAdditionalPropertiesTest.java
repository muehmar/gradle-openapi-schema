package com.github.muehmar.gradle.openapi.additionalproperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.additionalproperties.EnumAdditionalPropertiesDto.PropertyEnum;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class EnumAdditionalPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  private static final EnumAdditionalPropertiesDto DTO =
      EnumAdditionalPropertiesDto.builder()
          .setName("Dexter")
          .andOptionals()
          .addAdditionalProperty("color", PropertyEnum.ORANGE)
          .build();
  private static final String JSON = "{\"name\":\"Dexter\",\"color\":\"orange\"}";

  @Test
  void serialize_when_dto_then_correctJson() throws JsonProcessingException {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void deserialize_when_json_then_correctDto() throws JsonProcessingException {
    assertEquals(DTO, MAPPER.readValue(JSON, EnumAdditionalPropertiesDto.class));
  }
}
