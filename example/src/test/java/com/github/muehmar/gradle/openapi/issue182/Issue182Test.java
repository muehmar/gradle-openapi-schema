package com.github.muehmar.gradle.openapi.issue182;

import static com.github.muehmar.gradle.openapi.issue182.UserDataDto.userDataDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue182.UserDto.userDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue182Test {
  @Test
  void userDtoBuilder_when_used_thenCompilesAndCorrectAllOfStages() {
    final UserDto userDto =
        userDtoBuilder()
            .setFirstname("Dexter")
            .setLastname("Morgan")
            .setUserDataDto(userDataDtoBuilder().build())
            .setId(123)
            .build();

    assertNotNull(userDto);
  }
}
