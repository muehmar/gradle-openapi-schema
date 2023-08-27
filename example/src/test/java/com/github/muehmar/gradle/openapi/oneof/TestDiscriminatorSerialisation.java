package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import openapischema.example.api.oneof.model.AdminDto;
import openapischema.example.api.oneof.model.AdminOrUserDiscriminatorDto;
import openapischema.example.api.oneof.model.UserDto;
import org.junit.jupiter.api.Test;

class TestDiscriminatorSerialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("type")
            .setLevel(5L)
            .build();
    final AdminOrUserDiscriminatorDto dto = AdminOrUserDiscriminatorDto.fromAdmin(adminDto);

    assertEquals(
        "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDto_then_correctJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setType("type")
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminOrUserDiscriminatorDto dto = AdminOrUserDiscriminatorDto.fromUser(userDto);

    assertEquals(
        "{\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
        MAPPER.writeValueAsString(dto));
  }
}
