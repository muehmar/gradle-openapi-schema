package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscriminatorDeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5,\"color\":\"yellow\",\"type\":\"Admin\"}",
            AdminOrUserDiscriminatorDto.class);

    final Object result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .setType("Admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(AdminDto.ColorEnum.YELLOW)
            .build();

    assertEquals(adminDto, result);
    assertEquals(Optional.empty(), adminOrUserDto.getUserDto());
    assertEquals(Optional.of(adminDto), adminOrUserDto.getAdminDto());
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"type\":\"User\"}",
            AdminOrUserDiscriminatorDto.class);

    final Object result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .setType("User")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, result);
  }

  @Test
  void fold_when_matchesAdminAndUserButDiscriminatorIsAdmin_then_adminDtoDtoReturned()
      throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"adminname\":\"admin-name\",\"type\":\"Admin\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    final Object result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("id")
            .setAdminname("admin-name")
            .setType("Admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .addAdditionalProperty("username", "user-name")
            .addAdditionalProperty("age", 25)
            .addAdditionalProperty("email", Tristate.ofNull())
            .build();

    assertEquals(adminDto, result);
  }
}
