package com.github.muehmar.gradle.openapi.issues.issue195;

import static com.github.muehmar.gradle.openapi.issues.issue195.UserDto.userDtoBuilder;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue195Test {
  @Test
  void userDtoBuilder_when_used_then_requiredAdditionalPropertyStagePresentForAllOfSubPojo() {
    final UserDto dto = userDtoBuilder().setId("id").setName("name").andAllOptionals().build();

    assertNotNull(dto);
  }
}
