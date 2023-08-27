package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import openapischema.example.api.oneof.model.AdminDto;
import openapischema.example.api.oneof.model.AdminOrUserDiscriminatorDto;
import openapischema.example.api.oneof.model.UserDto;
import org.junit.jupiter.api.Test;

class TestDiscriminatorDeserialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.fold(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("Admin")
            .setLevel(5L)
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.fold(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setType("User")
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, obj);
  }

  @Test
  void fold_when_invalidTypeWithoutOnInvalid_then_exceptionThrown() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"invalid\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    assertThrows(
        IllegalStateException.class, () -> adminOrUserDto.fold(admin -> admin, user -> user));
  }

  @Test
  void fold_when_invalidTypeWithOnInvalid_then_onInvalidReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"invalid\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.fold(admin -> admin, user -> user, () -> "invalid");

    assertEquals("invalid", obj);
  }
}
