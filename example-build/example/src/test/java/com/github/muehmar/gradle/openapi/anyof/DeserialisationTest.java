package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5,\"color\":\"yellow\",\"type\":\"admin\"}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .setType("admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(AdminDto.ColorEnum.YELLOW)
            .build();

    assertEquals(Collections.singletonList(adminDto), result);
    assertEquals(Optional.empty(), adminOrUserDto.getUserDto());
    assertEquals(Optional.of(adminDto), adminOrUserDto.getAdminDto());
  }

  @Test
  void fold_when_matchesAdminOfInlined_then_adminDtoReturned() throws Exception {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"admin-id\",\"adminname\":\"admin-name\",\"level\":5.5,\"type\":\"admin\"}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .setType("admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();

    assertEquals(Collections.singletonList(adminDto), result);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"type\":\"user\"}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .setType("user")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(Collections.singletonList(userDto), result);
  }

  @Test
  void fold_when_matchesUserOfInlinedDto_then_userDtoReturned() throws Exception {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"type\":\"user\"}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .setType("user")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(Collections.singletonList(userDto), result);
  }

  @Test
  void fold_when_matchesAdminAndUser_then_adminDtoAndUserDtoReturned() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"adminname\":\"admin-name\",\"type\":\"type\",\"level\":5.5}",
            AdminOrUserDto.class);

    final List<Object> result = adminOrUserDto.foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("id")
            .setUsername("user-name")
            .setType("type")
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
            .setType("type")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .addAdditionalProperty("username", "user-name")
            .addAdditionalProperty("age", 25)
            .addAdditionalProperty("email", Tristate.ofNull())
            .build();

    final List<Object> expected = new ArrayList<>();
    expected.add(adminDto);
    expected.add(userDto);

    assertEquals(expected, result);
  }

  @Test
  void fold_when_matchesAdminAndUserOfInlinedDto_then_adminDtoAndUserDtoReturned()
      throws Exception {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"adminname\":\"admin-name\",\"level\":5.5,\"type\":\"type\"}}",
            InlinedAnyOfDto.class);

    final List<Object> result = inlinedDto.getAdminOrUser().foldAnyOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("id")
            .setUsername("user-name")
            .setType("type")
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
            .setType("type")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .addAdditionalProperty("username", "user-name")
            .addAdditionalProperty("age", 25)
            .addAdditionalProperty("email", Tristate.ofNull())
            .build();

    final List<Object> expected = new ArrayList<>();
    expected.add(adminDto);
    expected.add(userDto);

    assertEquals(expected, result);
  }
}
