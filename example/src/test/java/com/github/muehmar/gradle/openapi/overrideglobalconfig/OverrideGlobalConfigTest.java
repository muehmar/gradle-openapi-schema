package com.github.muehmar.gradle.openapi.overrideglobalconfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class OverrideGlobalConfigTest {
  @Test
  void userBuilder_when_used_thenCorrectSettersAndDtoName() {
    final User user =
        User.userBuilder()
            .andAllOptionals()
            .firstName("FirstName")
            .lastName("LastName")
            .street("Street")
            .houseNumber(1234)
            .build();

    assertNotNull(user);
  }
}
