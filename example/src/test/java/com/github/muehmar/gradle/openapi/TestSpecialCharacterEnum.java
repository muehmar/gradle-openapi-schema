package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.v1.model.SpecialCharacterEnumDto;
import org.junit.jupiter.api.Test;

class TestSpecialCharacterEnum {
  @Test
  void testCorrectConversion() {
    assertEquals("EVENT:SUB_EVENT", SpecialCharacterEnumDto.EVENT_SUB_EVENT.getValue());
    assertEquals("123456", SpecialCharacterEnumDto._123456.getValue());
    assertEquals("EVENT:*WILDCARD", SpecialCharacterEnumDto.EVENT_WILDCARD.getValue());
  }
}
