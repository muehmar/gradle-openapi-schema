package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import openapischema.example.api.anyof.model.AdminDto;
import openapischema.example.api.anyof.model.AdminOrUserAnyOfContainerDto;
import openapischema.example.api.anyof.model.AdminOrUserDto;
import openapischema.example.api.anyof.model.UserDto;
import org.junit.jupiter.api.Test;

class AnyOfContainerTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();

    final AdminOrUserAnyOfContainerDto container = AdminOrUserAnyOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserDto dto = AdminOrUserDto.newBuilder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"id\":\"admin-id\",\"level\":5}",
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

    final AdminOrUserAnyOfContainerDto container = AdminOrUserAnyOfContainerDto.fromUser(userDto);

    final AdminOrUserDto dto = AdminOrUserDto.newBuilder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"age\":25,\"email\":null,\"id\":\"user-id\",\"username\":\"user-name\"}",
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
            .setColor(Optional.empty())
            .build();

    final AdminOrUserAnyOfContainerDto c1 = AdminOrUserAnyOfContainerDto.fromUser(userDto);
    final AdminOrUserAnyOfContainerDto c2 = AdminOrUserAnyOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserAnyOfContainerDto container = c1.merge(c2);

    final AdminOrUserDto dto = AdminOrUserDto.newBuilder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"age\":25,\"email\":null,\"id\":\"id\",\"level\":5,\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }
}
