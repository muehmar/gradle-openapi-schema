package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"type\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setType("type")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();
    assertEquals(adminDto, obj);
    assertEquals(Optional.empty(), adminOrUserDto.getUserDto());
    assertEquals(Optional.of(adminDto), adminOrUserDto.getAdminDto());
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"type\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setType("type")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, obj);
  }
}
