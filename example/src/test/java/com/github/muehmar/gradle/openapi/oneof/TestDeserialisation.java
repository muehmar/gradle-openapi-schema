package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import openapischema.example.api.oneof.model.AdminDto;
import openapischema.example.api.oneof.model.AdminOrUserDto;
import openapischema.example.api.oneof.model.UserDto;
import org.junit.jupiter.api.Test;

class TestDeserialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"type\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("type")
            .setLevel(5L)
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"type\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setType("type")
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, obj);
  }
}
