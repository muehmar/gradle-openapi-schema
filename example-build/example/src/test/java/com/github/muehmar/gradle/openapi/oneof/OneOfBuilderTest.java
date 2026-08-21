package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OneOfBuilderTest {
  @Test
  void build_when_adminDtoPassed_then_storeOnlyAdmin() {
    final AdminDto adminDto =
        AdminDto.builder().setId("12345").setType("example-admin").setAdminname("John Doe").build();

    final AdminOrUserDto adminOrUserDto = AdminOrUserDto.builder().setAdminDto(adminDto).build();

    assertAll(
        () -> assertEquals(Optional.of(adminDto), adminOrUserDto.getAdminDto()),
        () -> assertEquals(Optional.empty(), adminOrUserDto.getUserDto()),
        () -> assertEquals(Collections.emptyList(), adminOrUserDto.getAdditionalProperties()));
  }

  @Test
  void build_when_userDtoPassed_then_storeOnlyUser() {
    final UserDto userDto =
        UserDto.builder().setId("12345").setType("example-user").setUsername("John Doe").build();

    final AdminOrUserDto adminOrUserDto = AdminOrUserDto.builder().setUserDto(userDto).build();

    assertAll(
        () -> assertEquals(Optional.empty(), adminOrUserDto.getAdminDto()),
        () -> assertEquals(Optional.of(userDto), adminOrUserDto.getUserDto()),
        () -> assertEquals(Collections.emptyList(), adminOrUserDto.getAdditionalProperties()));
  }
}
