package com.github.muehmar.gradle.openapi.issues.issue182;

import static com.github.muehmar.gradle.openapi.issues.issue182.UserDto.userDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue182.UserNameDto.userNameDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue182Test {
  @Test
  void userDtoBuilder_when_onlyPropertyStagesUsed_thenCompilesAndCorrectAllOfStages() {
    final UserDto userDto =
        userDtoBuilder().setFirstname("Dexter").setLastname("Morgan").setId(123).build();

    assertNotNull(userDto);
  }

  @Test
  void userDtoBuilder_when_onlyDtoStagesUsed_thenCompilesAndCorrectAllOfStages() {
    final UserDto userDto;
    userDto =
        userDtoBuilder()
            .setUserNameDto(
                userNameDtoBuilder().setFirstname("Dexter").setLastname("Morgan").build())
            .setUserDataDto(UserDataDto.userDataDtoBuilder().build())
            .setId(123)
            .build();

    assertNotNull(userDto);
  }
}
