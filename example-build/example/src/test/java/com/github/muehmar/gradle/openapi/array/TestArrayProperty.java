package com.github.muehmar.gradle.openapi.array;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class TestArrayProperty {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static final ReferenceArrayPropertyDto DTO =
      ReferenceArrayPropertyDto.builder()
          .setPosology(new PosologyDto(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 4.0))))
          .build();
  private static final String JSON = "{\"posology\":[1.0,2.0,3.0,4.0]}";

  @Test
  void deserialize_when_referenceArrayPropertyDto_then_correctDto() throws Exception {
    final ReferenceArrayPropertyDto deserializedDto =
        MAPPER.readValue(JSON, ReferenceArrayPropertyDto.class);
    assertEquals(DTO, deserializedDto);
  }

  @Test
  void serialize_when_referenceArrayPropertyDto_then_correctJson() throws Exception {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }
}
