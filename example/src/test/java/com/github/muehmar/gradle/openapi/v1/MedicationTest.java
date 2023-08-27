package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import openapischema.example.api.v1.model.MedicationDto;
import org.junit.jupiter.api.Test;

class MedicationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void deserialize_when_json_then_correctDto() throws JsonProcessingException {
    final MedicationDto medicationDto =
        MAPPER.readValue("{\"name\":\"Dafalgan\",\"kind\":\"NEW\"}", MedicationDto.class);

    assertEquals("Dafalgan", medicationDto.getName());
    assertEquals(MedicationKind.NEW, medicationDto.getKind());
  }

  @Test
  void serialize_when_dto_then_correctJson() throws JsonProcessingException {
    final MedicationDto dto =
        MedicationDto.newBuilder().setName("Dafalgan").setKind(MedicationKind.DELETED).build();

    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"kind\":\"DELETED\",\"name\":\"Dafalgan\"}", json);
  }
}
