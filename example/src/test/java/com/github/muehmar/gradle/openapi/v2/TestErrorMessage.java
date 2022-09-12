package com.github.muehmar.gradle.openapi.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.v2.model.ErrorMessageDto;
import org.junit.jupiter.api.Test;

class TestErrorMessage {
  @Test
  void newBuilder_when_builderUsed_then_correctPopulated() {
    final ErrorMessageDto dto =
        ErrorMessageDto.newBuilder()
            .key(ErrorMessageDto.KeyEnum.A2)
            .message("Error for A2")
            .build();

    assertEquals(ErrorMessageDto.KeyEnum.A2, dto.getKey());
    assertEquals("Error for A2", dto.getMessage());
  }
}
