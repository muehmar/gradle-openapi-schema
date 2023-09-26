package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import openapischema.example.api.v1.model.SampleDto;
import org.junit.jupiter.api.Test;

class FullBuilderTest {
  @Test
  void sampleFullBuilder_when_called_then_needsToSetAllProperties() {
    final SampleDto dto =
        SampleDto.fullBuilder().setProp1("prop1").setProp2(2).setProp3("prop3").setProp4(4).build();

    assertEquals("prop1", dto.getProp1());
    assertEquals(2, dto.getProp2());
    assertEquals(Optional.of("prop3"), dto.getProp3Opt());
    assertEquals(Optional.of(4), dto.getProp4Opt());
  }
}
