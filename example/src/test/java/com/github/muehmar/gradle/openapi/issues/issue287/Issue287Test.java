package com.github.muehmar.gradle.openapi.issues.issue287;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class Issue287Test {
  @Test
  void userDtoBuilder_when_used_then_itemHasIntegerType() {
    final UserDto userDto = UserDto.fullUserDtoBuilder().setRoleIds(Arrays.asList(5, 10)).build();

    assertEquals(Arrays.asList(5, 10), userDto.getRoleIdsOr(Collections.emptyList()));
  }
}
