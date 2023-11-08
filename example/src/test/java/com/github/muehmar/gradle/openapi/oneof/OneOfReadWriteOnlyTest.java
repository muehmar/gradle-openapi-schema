package com.github.muehmar.gradle.openapi.oneof;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class OneOfReadWriteOnlyTest {
  @Test
  void getDeclaredField_when_ageForUserDto_then_fieldExists() throws NoSuchFieldException {
    final Field ageField = UserDto.class.getDeclaredField("age");
    assertNotNull(ageField);
  }

  @Test
  void getDeclaredField_when_ageForAdminOrUserDto_then_fieldExists() throws NoSuchFieldException {
    final Field ageField = AdminOrUserDto.class.getDeclaredField("age");
    assertNotNull(ageField);
  }

  @Test
  void getDeclaredField_when_ageForUserRequestDto_then_fieldExists() throws NoSuchFieldException {
    final Field ageField = UserRequestDto.class.getDeclaredField("age");
    assertNotNull(ageField);
  }

  @Test
  void getDeclaredField_when_ageForAdminOrUserRequestDto_then_fieldExists()
      throws NoSuchFieldException {
    final Field ageField = AdminOrUserRequestDto.class.getDeclaredField("age");
    assertNotNull(ageField);
  }

  @Test
  void getDeclaredField_when_ageForUserResponseDto_then_fieldDoesNotExist() {
    assertThrows(NoSuchFieldException.class, () -> UserResponseDto.class.getDeclaredField("age"));
  }

  @Test
  void getDeclaredField_when_ageForAdminOrUserResponseDto_then_fieldDoesNotExists() {
    assertThrows(
        NoSuchFieldException.class, () -> AdminOrUserResponseDto.class.getDeclaredField("age"));
  }
}
