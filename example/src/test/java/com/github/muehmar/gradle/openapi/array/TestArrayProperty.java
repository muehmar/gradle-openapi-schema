package com.github.muehmar.gradle.openapi.array;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.arrayproperty.model.CustomerDto;
import OpenApiSchema.example.api.arrayproperty.model.PosologyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class TestArrayProperty {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void testDeserialisation() throws JsonProcessingException {
    final String json = "{\"posology\":[1.0,2.0,3.0,4.0]}";
    final CustomerDto customerDto = MAPPER.readValue(json, CustomerDto.class);

    assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0), customerDto.getPosology().getValue());
  }

  @Test
  void testSerialisation() throws JsonProcessingException {
    final PosologyDto posology =
        PosologyDto.newBuilder()
            .setValue(new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 4.0)))
            .build();
    final CustomerDto customerDto = CustomerDto.newBuilder().setPosology(posology).build();
    final String json1 = MAPPER.writeValueAsString(customerDto);

    assertEquals("{\"posology\":[1.0,2.0,3.0,4.0]}", json1);
  }
}
