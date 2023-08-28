package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import openapischema.example.api.oneof.model.AdminDto;
import openapischema.example.api.oneof.model.AdminOrUserDiscriminatorDto;
import openapischema.example.api.oneof.model.AdminOrUserDiscriminatorOneOfContainerDto;
import openapischema.example.api.oneof.model.UserDto;
import org.junit.jupiter.api.Test;

class OneOfContainerTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setType("type")
            .setLevel(5L)
            .build();

    final AdminOrUserDiscriminatorOneOfContainerDto container =
        AdminOrUserDiscriminatorOneOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setOneOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"id\":\"admin-id\",\"level\":5,\"type\":\"Admin\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDto_then_correctJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setType("type")
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();

    final AdminOrUserDiscriminatorOneOfContainerDto container =
        AdminOrUserDiscriminatorOneOfContainerDto.fromUser(userDto);

    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setOneOfContainer(container).build();

    assertEquals(
        "{\"age\":25,\"email\":null,\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }
}
