package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.anyof.model.AdminDto;
import OpenApiSchema.example.api.anyof.model.AdminOrUserDto;
import OpenApiSchema.example.api.anyof.model.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestDeserialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.fold(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();

    assertEquals(Collections.singletonList(adminDto), result);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.fold(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.newBuilder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(Collections.singletonList(userDto), result);
  }

  @Test
  void fold_when_matchesAdminAndUser_then_adminDtoAndUserDtoReturned()
      throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.fold(admin -> admin, user -> user);

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

    final List<Object> expected = new ArrayList<>();
    expected.add(adminDto);
    expected.add(userDto);

    assertEquals(expected, result);
  }
}
