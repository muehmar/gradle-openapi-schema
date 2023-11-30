package com.github.muehmar.gradle.openapi.issue193;

import static com.github.muehmar.gradle.openapi.issue193.UserDto.userDtoBuilder;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue193Test {
  @Test
  void userDtoBuilder_when_used_then_requiredAdditionalPropertyStagePresent() {
    final UserDto dto =
        userDtoBuilder().setStreet("street").andAllOptionals().setName("name").build();

    assertNotNull(dto);
  }
}
