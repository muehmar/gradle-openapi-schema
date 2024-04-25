package com.github.muehmar.gradle.openapi.issues.issue229;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class DiscriminatorDeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"admin\",\"adminname\":\"admin-name\"}", AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setAdminname("admin-name")
            .setUsertype(UserTypeDto.ADMIN)
            .andAllOptionals()
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"user\",\"username\":\"user-name\"}", AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setUsername("user-name")
            .setUsertype(UserTypeDto.USER)
            .andAllOptionals()
            .build();
    assertEquals(userDto, obj);
  }
}
