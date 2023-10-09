package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import openapischema.example.api.anyof.model.AdminDto;
import openapischema.example.api.anyof.model.AdminOrUserDto;
import openapischema.example.api.anyof.model.InlinedAnyOfDto;
import openapischema.example.api.anyof.model.UserDto;
import org.junit.jupiter.api.Test;

class DeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5,\"color\":\"yellow\"}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(AdminDto.ColorEnum.YELLOW)
            .build();

    assertEquals(Collections.singletonList(adminDto), result);
    assertEquals(Optional.empty(), adminOrUserDto.getUserDto());
    assertEquals(Optional.of(adminDto), adminOrUserDto.getAdminDto());
  }

  @Test
  void fold_when_matchesAdminOfInlined_then_adminDtoReturned() throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();

    assertEquals(Collections.singletonList(adminDto), result);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(Collections.singletonList(userDto), result);
  }

  @Test
  void fold_when_matchesUserOfInlinedDto_then_userDtoReturned() throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
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

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .addAdditionalProperty("adminname", "admin-name")
            .addAdditionalProperty("level", 5L)
            .build();

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .addAdditionalProperty("username", "user-name")
            .addAdditionalProperty("age", 25)
            .addAdditionalProperty("email", null)
            .build();

    final List<Object> expected = new ArrayList<>();
    expected.add(adminDto);
    expected.add(userDto);

    assertEquals(expected, result);
  }

  @Test
  void fold_when_matchesAdminAndUserOfInlinedDto_then_adminDtoAndUserDtoReturned()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"adminname\":\"admin-name\",\"level\":5.5}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("id")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .addAdditionalProperty("adminname", "admin-name")
            .addAdditionalProperty("level", 5L)
            .build();

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("id")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .addAdditionalProperty("username", "user-name")
            .addAdditionalProperty("age", 25)
            .addAdditionalProperty("email", null)
            .build();

    final List<Object> expected = new ArrayList<>();
    expected.add(adminDto);
    expected.add(userDto);

    assertEquals(expected, result);
  }
}
