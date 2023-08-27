package com.github.muehmar.gradle.openapi.readwriteonly;

import static org.junit.jupiter.api.Assertions.assertEquals;

import openapischema.example.api.readwriteonly.model.ReadWriteDto;
import openapischema.example.api.readwriteonly.model.ReadWriteRequestDto;
import openapischema.example.api.readwriteonly.model.ReadWriteResponseDto;
import org.junit.jupiter.api.Test;

class ReadWriteOnlyTest {
  @Test
  void newBuilder_when_calledForDefaultDto_then_readOnlyAndWriteOnlyPropertiesPresent() {
    final ReadWriteDto dto =
        ReadWriteDto.newBuilder()
            .setId("id")
            .setReadOnly("readOnly")
            .setWriteOnly("writeOnly")
            .andAllOptionals()
            .build();

    assertEquals("id", dto.getId());
    assertEquals("readOnly", dto.getReadOnly());
    assertEquals("writeOnly", dto.getWriteOnly());
  }

  @Test
  void newBuilder_when_calledForResponseDto_then_writeOnlyPropertyNotPresent() {
    final ReadWriteResponseDto dto =
        ReadWriteResponseDto.newBuilder()
            .setId("id")
            .setReadOnly("readOnly")
            .andAllOptionals()
            .build();

    assertEquals("id", dto.getId());
    assertEquals("readOnly", dto.getReadOnly());
  }

  @Test
  void newBuilder_when_calledForRequestDto_then_readOnlyPropertyNotPresent() {
    final ReadWriteRequestDto dto =
        ReadWriteRequestDto.newBuilder()
            .setId("id")
            .setWriteOnly("writeOnly")
            .andAllOptionals()
            .build();

    assertEquals("id", dto.getId());
    assertEquals("writeOnly", dto.getWriteOnly());
  }
}
