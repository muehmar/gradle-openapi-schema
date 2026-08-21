package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class MedicationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final MedicationDto medicationDto =
        MAPPER.readValue("{\"name\":\"Dafalgan\",\"kind\":\"NEW\"}", MedicationDto.class);

    assertEquals("Dafalgan", medicationDto.getName());
    assertEquals(MedicationKind.NEW, medicationDto.getKind());
  }

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    final MedicationDto dto =
        MedicationDto.builder().setName("Dafalgan").setKind(MedicationKind.DELETED).build();

    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"kind\":\"DELETED\",\"name\":\"Dafalgan\"}", json);
  }
}
