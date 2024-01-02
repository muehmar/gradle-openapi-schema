package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

class DiscriminatorMappingDeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorMappingDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"adm\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorMappingDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setType("adm")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorMappingDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"usr\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDiscriminatorMappingDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setType("usr")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, obj);
  }

  @Test
  void fold_when_invalidTypeWithoutOnInvalid_then_exceptionThrown() throws JsonProcessingException {
    final AdminOrUserDiscriminatorMappingDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorMappingDto.class);

    assertThrows(
        IllegalStateException.class, () -> adminOrUserDto.foldOneOf(admin -> admin, user -> user));
  }

  @Test
  void fold_when_invalidTypeWithOnInvalid_then_onInvalidReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorMappingDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorMappingDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user, () -> "invalid");

    assertEquals("invalid", obj);
  }
}
