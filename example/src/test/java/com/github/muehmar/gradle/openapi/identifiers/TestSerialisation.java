package com.github.muehmar.gradle.openapi.identifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.identifiers.model.New;
import OpenApiSchema.example.api.identifiers.model.User1;
import OpenApiSchema.example.api.identifiers.model.User1OrUser2;
import OpenApiSchema.example.api.identifiers.model.User2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TestSerialisation {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void writeValueAsString_when_user1Dto_then_correctJson() throws JsonProcessingException {
    final User1 dto1 =
        User1.newBuilder().setNew("new").andAllOptionals().setInterface(Optional.empty()).build();
    assertEquals("{\"new\":\"new\"}", MAPPER.writeValueAsString(dto1));
  }

  @Test
  void writeValueAsString_when_user2DtoInAnyOf_then_correctJson() throws JsonProcessingException {
    final User2 dto2 =
        User2.newBuilder().setPublic("public").andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 dto = User1OrUser2.fromUser2(dto2);

    assertEquals("{\"public\":\"public\",\"null\":null}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_newDto_then_correctJson() throws JsonProcessingException {
    final New dto = New.newBuilder().andAllOptionals().setBar("bar").build();

    assertEquals("{\"bar\":\"bar\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void readValue_when_user1Dto_then_correctDto() throws JsonProcessingException {
    final User1 user1Dto =
        MAPPER.readValue("{\"new\":\"new\",\"interface\":\"interface\"}", User1.class);
    final User1 expectedDto =
        User1.newBuilder().setNew("new").andAllOptionals().setInterface("interface").build();
    assertEquals(expectedDto, user1Dto);
  }

  @Test
  void readValue_when_user2DtoInAnyOf_then_correctDto() throws JsonProcessingException {
    final User1OrUser2 dto =
        MAPPER.readValue("{\"public\":\"public\",\"null\":null}", User1OrUser2.class);
    final User2 dto2 =
        User2.newBuilder().setPublic("public").andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 expectedDto = User1OrUser2.fromUser2(dto2);
    assertEquals(expectedDto, dto);
  }

  @Test
  void readValue_when_newDto_then_correctDto() throws JsonProcessingException {
    final New dto = MAPPER.readValue("{\"bar\":\"bar\"}", New.class);
    final New expectedDto = New.newBuilder().andAllOptionals().setBar("bar").build();
    assertEquals(expectedDto, dto);
  }
}
