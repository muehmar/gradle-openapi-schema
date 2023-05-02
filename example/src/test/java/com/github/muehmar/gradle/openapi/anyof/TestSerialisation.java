package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.anyof.model.AdminDto;
import OpenApiSchema.example.api.anyof.model.AdminOrUserDto;
import OpenApiSchema.example.api.anyof.model.InlinedAnyOfAdminOrUserDto;
import OpenApiSchema.example.api.anyof.model.InlinedAnyOfDto;
import OpenApiSchema.example.api.anyof.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

class TestSerialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();
    final AdminOrUserDto dto = AdminOrUserDto.fromAdmin(adminDto);

    assertEquals(
        "{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_adminDtoOfInlinedDto_then_correctJson()
      throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();
    final InlinedAnyOfDto dto =
        InlinedAnyOfDto.newBuilder()
            .setAdminOrUser(InlinedAnyOfAdminOrUserDto.fromAdmin(adminDto))
            .build();

    assertEquals(
        "{\"adminOrUser\":{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5}}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDto_then_correctJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminOrUserDto dto = AdminOrUserDto.fromUser(userDto);

    assertEquals(
        "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDtoOfInlinedDto_then_correctJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();

    final InlinedAnyOfDto dto =
        InlinedAnyOfDto.newBuilder()
            .setAdminOrUser(InlinedAnyOfAdminOrUserDto.fromUser(userDto))
            .build();

    assertEquals(
        "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_adminAndUserDto_then_correctJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId("id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();

    final AdminOrUserDto dto = AdminOrUserDto.fromUser(userDto).withAdmin(adminDto);

    assertEquals(
        "{\"id\":\"id\",\"adminname\":\"admin-name\",\"level\":5,\"username\":\"user-name\",\"age\":25,\"email\":null}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_adminAndUserOfInlinedDto_then_correctJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId("id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();

    final InlinedAnyOfDto dto =
        InlinedAnyOfDto.newBuilder()
            .setAdminOrUser(InlinedAnyOfAdminOrUserDto.fromAdmin(adminDto).withUser(userDto))
            .build();

    assertEquals(
        "{\"adminOrUser\":{\"id\":\"id\",\"adminname\":\"admin-name\",\"level\":5,\"username\":\"user-name\",\"age\":25,\"email\":null}}",
        MAPPER.writeValueAsString(dto));
  }
}
