package com.github.muehmar.gradle.openapi.issues.issue275;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class Issue275Test {
  @Test
  void fromItems_when_integerList_then_returnSameIntegerList() {
    final RoleIdsDto roleIdsDto = RoleIdsDto.fromItems(Arrays.asList(1, 2, 3));

    assertEquals(Arrays.asList(1, 2, 3), roleIdsDto.getItems());
  }
}
