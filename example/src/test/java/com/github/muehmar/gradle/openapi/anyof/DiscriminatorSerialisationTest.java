package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscriminatorSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws JsonProcessingException {
    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .setType("admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();
    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setAdminDto(adminDto).build();

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
            .setType("user")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setUserDto(userDto).build();

    assertEquals(
        "{\"age\":25,\"email\":null,\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }
}
