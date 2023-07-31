package com.github.muehmar.gradle.openapi.array;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.ArrayList;
import java.util.Arrays;
import openapischema.example.api.array.model.PosologyDto;
import openapischema.example.api.array.model.ReferenceArrayPropertyDto;
import org.junit.jupiter.api.Test;

class TestArrayProperty {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  private static final ReferenceArrayPropertyDto DTO =
      ReferenceArrayPropertyDto.newBuilder()
          .setPosology(new PosologyDto(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 4.0))))
          .build();
  private static final String JSON = "{\"posology\":[1.0,2.0,3.0,4.0]}";

  @Test
  void deserialize_when_referenceArrayProprtyDto_then_correctDto() throws JsonProcessingException {
    final ReferenceArrayPropertyDto deserializedDto =
        MAPPER.readValue(JSON, ReferenceArrayPropertyDto.class);
    assertEquals(DTO, deserializedDto);
  }

  @Test
  void serialize_when_() throws JsonProcessingException {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }
}
