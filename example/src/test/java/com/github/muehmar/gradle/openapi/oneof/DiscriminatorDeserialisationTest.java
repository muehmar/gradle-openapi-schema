package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

class DiscriminatorDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"Admin\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setType("Admin")
            .setAdminname("admin-name")
            .andAllOptionals()
            .setLevel(5L)
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setType("User")
            .setUsername("user-name")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    assertEquals(userDto, obj);
  }

  @Test
  void fold_when_invalidTypeWithoutOnInvalid_then_exceptionThrown() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"invalid\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    assertThrows(
        IllegalStateException.class, () -> adminOrUserDto.foldOneOf(admin -> admin, user -> user));
  }

  @Test
  void fold_when_invalidTypeWithOnInvalid_then_onInvalidReturned() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"admin-id\",\"type\":\"invalid\",\"adminname\":\"admin-name\",\"level\":5.5}",
            AdminOrUserDiscriminatorDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user, () -> "invalid");

    assertEquals("invalid", obj);
  }
}
