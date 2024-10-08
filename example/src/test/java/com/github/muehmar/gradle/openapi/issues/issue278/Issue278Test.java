package com.github.muehmar.gradle.openapi.issues.issue278;

import static com.github.muehmar.gradle.openapi.issues.issue278.FullObjectDto.fullFullObjectDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue278.NestedFullObjectDto.fullNestedFullObjectDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue278.UserDto.fullUserDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class Issue278Test {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_userDtoUsed_then_requiredNullablePropertyFromAdminDtoIsNotSerialized()
      throws JsonProcessingException {
    final UserDto userDto =
        fullUserDtoBuilder().setType("type-user").setUsername("username-user").build();

    final FullObjectDto fullObjectDto = fullFullObjectDtoBuilder().setUserDto(userDto).build();

    final NestedFullObjectDto nestedFullObjectDto =
        fullNestedFullObjectDtoBuilder().setFullObjectDto(fullObjectDto).setAmount(5).build();

    final String json = MAPPER.writeValueAsString(nestedFullObjectDto);

    assertEquals("{\"amount\":5,\"type\":\"type-user\",\"username\":\"username-user\"}", json);
  }
}
