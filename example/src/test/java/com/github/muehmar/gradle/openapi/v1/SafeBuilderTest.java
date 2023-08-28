package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import openapischema.example.api.v1.model.SampleDto;
import org.junit.jupiter.api.Test;

class SafeBuilderTest {
  @Test
  void andAllOptionals_when_useEmptyAndNonEmptyOptionals_then_allValuesCorrectSet() {
    final SampleDto dto =
        SampleDto.builder()
            .setProp1("prop1")
            .setProp2(5)
            .andAllOptionals()
            .setProp3(Optional.of("prop3"))
            .setProp4(Optional.empty())
            .build();

    assertEquals(Optional.of("prop3"), dto.getProp3Opt());
    assertEquals(Optional.empty(), dto.getProp4Opt());
  }

  @Test
  void andOptionals_when_useEmptyAndNonEmptyOptionals_then_allValuesCorrectSet() {
    final SampleDto dto =
        SampleDto.builder()
            .setProp1("prop1")
            .setProp2(5)
            .andOptionals()
            .setProp3(Optional.of("prop3"))
            .setProp4(Optional.empty())
            .build();

    assertEquals(Optional.of("prop3"), dto.getProp3Opt());
    assertEquals(Optional.empty(), dto.getProp4Opt());
  }
}
