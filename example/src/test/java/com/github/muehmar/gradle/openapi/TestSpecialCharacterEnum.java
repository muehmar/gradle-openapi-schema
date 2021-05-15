package com.github.muehmar.gradle.openapi;

import OpenApiSchema.example.api.model.SpecialCharacterEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSpecialCharacterEnum {
  @Test
  void testCorrectConversion() {
    assertEquals("EVENT:SUB_EVENT", SpecialCharacterEnumDto.EVENT_SUB_EVENT.getValue());
    assertEquals("123456", SpecialCharacterEnumDto._123456.getValue());
    assertEquals("EVENT:*WILDCARD", SpecialCharacterEnumDto.EVENT_WILDCARD.getValue());
  }
}
