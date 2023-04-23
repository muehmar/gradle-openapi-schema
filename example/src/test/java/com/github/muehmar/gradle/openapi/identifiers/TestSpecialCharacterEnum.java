package com.github.muehmar.gradle.openapi.identifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.identifiers.model.Special_Character_Enum;
import org.junit.jupiter.api.Test;

class TestSpecialCharacterEnum {
  @Test
  void getValue_when_called_then_correctString() {
    assertEquals("EVENT:SUB_EVENT", Special_Character_Enum.EVENT_SUB_EVENT.getValue());
    assertEquals("123456", Special_Character_Enum._123456.getValue());
    assertEquals("EVENT:*WILDCARD", Special_Character_Enum.EVENT_WILDCARD.getValue());
  }
}
