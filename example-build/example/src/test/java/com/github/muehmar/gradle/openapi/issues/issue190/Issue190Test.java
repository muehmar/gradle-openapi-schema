package com.github.muehmar.gradle.openapi.issues.issue190;

import static com.github.muehmar.gradle.openapi.issues.issue190.Leaf1ADto.leaf1ADtoBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue190Test {

  @Test
  void generatedCodeCompiles() {
    final Root2Dto dto =
        Root2Dto.root2DtoBuilder()
            .setLeaf1ADto(leaf1ADtoBuilder().build())
            .andAllOptionals()
            .setProp1(1)
            .setProp2(2)
            .build();

    assertNotNull(dto);
  }
}
