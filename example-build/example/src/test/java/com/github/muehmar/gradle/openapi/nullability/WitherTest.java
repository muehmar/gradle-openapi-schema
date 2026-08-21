package com.github.muehmar.gradle.openapi.nullability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WitherTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void withId_when_called_then_newDtoContainsNewId() throws Exception {
    final UserDto initialDto = UserDto.builder().setId("id").setUsername("username").build();

    final UserDto newDto = initialDto.withId("newId");

    assertEquals("newId", newDto.getId());
    assertEquals("{\"id\":\"newId\",\"username\":\"username\"}", MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withUsername_when_calledWithValue_then_newDtoContainsNewUsername() throws Exception {
    final UserDto initialDto = UserDto.builder().setId("id").setUsername("username").build();

    final UserDto newDto = initialDto.withUsername("newUsername");

    assertEquals(Optional.of("newUsername"), newDto.getUsernameOpt());
    assertEquals("{\"id\":\"id\",\"username\":\"newUsername\"}", MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withUsername_when_calledWithNoValue_then_newDtoContainsNullUsername() throws Exception {
    final UserDto initialDto = UserDto.builder().setId("id").setUsername("username").build();

    final UserDto newDto = initialDto.withUsername(Optional.empty());

    assertEquals(Optional.empty(), newDto.getUsernameOpt());
    assertEquals("{\"id\":\"id\",\"username\":null}", MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withEmail_when_calledWithValue_then_newDtoContainsNewEmail() throws Exception {
    final UserDto initialDto =
        UserDto.builder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .setEmail("email")
            .build();

    final UserDto newDto = initialDto.withEmail("newEmail");

    assertEquals(Optional.of("newEmail"), newDto.getEmailOpt());
    assertEquals(
        "{\"email\":\"newEmail\",\"id\":\"id\",\"username\":\"username\"}",
        MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withEmail_when_calledWithNoValue_then_newDtoContainsNoEmail() throws Exception {
    final UserDto initialDto =
        UserDto.builder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .setEmail("email")
            .build();

    final UserDto newDto = initialDto.withEmail(Optional.empty());

    assertEquals(Optional.empty(), newDto.getEmailOpt());
    assertEquals("{\"id\":\"id\",\"username\":\"username\"}", MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withPhone_when_calledWithValue_then_newDtoContainsNewPhone() throws Exception {
    final UserDto initialDto =
        UserDto.builder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .setPhone("phone")
            .build();

    final UserDto newDto = initialDto.withPhone("newPhone");

    assertEquals(Tristate.ofValue("newPhone"), newDto.getPhoneTristate());
    assertEquals(
        "{\"id\":\"id\",\"phone\":\"newPhone\",\"username\":\"username\"}",
        MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withPhone_when_calledWithAbsent_then_newDtoContainsNoPhone() throws Exception {
    final UserDto initialDto =
        UserDto.builder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .setPhone("phone")
            .build();

    final UserDto newDto = initialDto.withPhone(Tristate.ofAbsent());

    assertEquals(Tristate.ofAbsent(), newDto.getPhoneTristate());
    assertEquals("{\"id\":\"id\",\"username\":\"username\"}", MAPPER.writeValueAsString(newDto));
  }

  @Test
  void withPhone_when_calledWithNull_then_newDtoContainsNullPhone() throws Exception {
    final UserDto initialDto =
        UserDto.builder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .setPhone("phone")
            .build();

    final UserDto newDto = initialDto.withPhone(Tristate.ofNull());

    assertEquals(Tristate.ofNull(), newDto.getPhoneTristate());
    assertEquals(
        "{\"id\":\"id\",\"phone\":null,\"username\":\"username\"}",
        MAPPER.writeValueAsString(newDto));
  }
}
