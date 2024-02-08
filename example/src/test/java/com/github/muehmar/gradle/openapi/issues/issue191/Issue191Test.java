package com.github.muehmar.gradle.openapi.issues.issue191;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class Issue191Test {
  @Test
  void root2DtoBuilder_when_used_then_oneOfStagePresent() {
    final Root2Dto dto =
        Root2Dto.root2DtoBuilder()
            .setLeaf1ADto(Leaf1ADto.builder().build())
            .andAllOptionals()
            .setProp1(1)
            .build();

    assertNotNull(dto);
  }
}
