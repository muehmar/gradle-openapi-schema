package com.github.muehmar.gradle.openapi.identifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import openapischema.example.api.identifiers.model.New;
import openapischema.example.api.identifiers.model.User1;
import openapischema.example.api.identifiers.model.User1OrUser2;
import openapischema.example.api.identifiers.model.User2;
import org.junit.jupiter.api.Test;

class TestSerialisation {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void writeValueAsString_when_user1Dto_then_correctJson() throws JsonProcessingException {
    final User1 dto1 =
        User1.builder().setNew("new").andAllOptionals().setInterface(Optional.empty()).build();
    assertEquals("{\"new\":\"new\"}", MAPPER.writeValueAsString(dto1));
  }

  @Test
  void writeValueAsString_when_user2DtoInAnyOf_then_correctJson() throws JsonProcessingException {
    final User2 dto2 =
        User2.builder().setPublic("public").andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 dto = User1OrUser2.builder().setUser2(dto2).build();

    assertEquals("{\"public\":\"public\",\"null\":null}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_newDto_then_correctJson() throws JsonProcessingException {
    final New dto = New.builder().andAllOptionals().setBar("bar").build();

    assertEquals("{\"bar\":\"bar\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void readValue_when_user1Dto_then_correctDto() throws JsonProcessingException {
    final User1 user1Dto =
        MAPPER.readValue("{\"new\":\"new\",\"interface\":\"interface\"}", User1.class);
    final User1 expectedDto =
        User1.builder().setNew("new").andAllOptionals().setInterface("interface").build();
    assertEquals(expectedDto, user1Dto);
  }

  @Test
  void readValue_when_user2DtoInAnyOf_then_correctDto() throws JsonProcessingException {
    final User1OrUser2 dto =
        MAPPER.readValue("{\"public\":\"public\",\"null\":null}", User1OrUser2.class);
    final User2 dto2 =
        User2.builder().setPublic("public").andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 expectedDto = User1OrUser2.builder().setUser2(dto2).build();
    assertEquals(expectedDto, dto);
  }

  @Test
  void readValue_when_newDto_then_correctDto() throws JsonProcessingException {
    final New dto = MAPPER.readValue("{\"bar\":\"bar\"}", New.class);
    final New expectedDto = New.builder().andAllOptionals().setBar("bar").build();
    assertEquals(expectedDto, dto);
  }
}
