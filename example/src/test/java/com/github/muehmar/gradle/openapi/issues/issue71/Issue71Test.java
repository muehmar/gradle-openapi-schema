package com.github.muehmar.gradle.openapi.issues.issue71;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class Issue71Test {

  @Test
  void equals_when_dtoHasPropertyNamedOther_then_dtoIsEqualsWithSameProperties() {
    final SchemaWithPropertyNameOtherDto dto1 =
        SchemaWithPropertyNameOtherDto.builder()
            .andAllOptionals()
            .setFoo("foo")
            .setOther("other")
            .build();
    final SchemaWithPropertyNameOtherDto dto2 =
        SchemaWithPropertyNameOtherDto.builder()
            .andAllOptionals()
            .setFoo("foo")
            .setOther("other")
            .build();
    assertEquals(dto1, dto2);
  }

  @Test
  void equals_when_dtoHasPropertyNamedOther_then_dtoIsNotEqualsWithDifferentProperties() {
    final SchemaWithPropertyNameOtherDto dto1 =
        SchemaWithPropertyNameOtherDto.builder()
            .andAllOptionals()
            .setFoo("foo1")
            .setOther("other")
            .build();
    final SchemaWithPropertyNameOtherDto dto2 =
        SchemaWithPropertyNameOtherDto.builder()
            .andAllOptionals()
            .setFoo("foo2")
            .setOther("other")
            .build();
    assertNotEquals(dto1, dto2);
  }
}
