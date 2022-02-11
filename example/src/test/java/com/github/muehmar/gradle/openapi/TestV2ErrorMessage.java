package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.v2.model.ErrorMessageDto;
import org.junit.jupiter.api.Test;

class TestV2ErrorMessage {
  @Test
  void newBuilder_when_builderUsed_then_correctPopulated() {
    final ErrorMessageDto dto =
        ErrorMessageDto.newBuilder()
            .setKey(ErrorMessageDto.KeyEnum.A2)
            .setMessage("Error for A2")
            .build();

    assertEquals(ErrorMessageDto.KeyEnum.A2, dto.getKey());
    assertEquals("Error for A2", dto.getMessage());
  }
}
