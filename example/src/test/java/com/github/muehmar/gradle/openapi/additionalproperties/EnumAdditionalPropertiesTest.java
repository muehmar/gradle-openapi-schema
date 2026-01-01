package com.github.muehmar.gradle.openapi.additionalproperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.additionalproperties.EnumAdditionalPropertiesDto.PropertyEnum;
import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class EnumAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static final EnumAdditionalPropertiesDto DTO =
      EnumAdditionalPropertiesDto.builder()
          .setName("Dexter")
          .andOptionals()
          .addAdditionalProperty("color", PropertyEnum.ORANGE)
          .build();
  private static final String JSON = "{\"name\":\"Dexter\",\"color\":\"orange\"}";

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    assertEquals(DTO, MAPPER.readValue(JSON, EnumAdditionalPropertiesDto.class));
  }
}
