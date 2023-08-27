package com.github.muehmar.gradle.openapi.identifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import openapischema.example.api.identifiers.model.SchemaWithPropertyNameOther;
import org.junit.jupiter.api.Test;

/** Tests for issue 71 */
class SchemaWithPropertyNameOtherTest {
  @Test
  void equals_when_dtoHasPropertyNamedOther_then_dtoIsEqualsWithSameProperties() {
    final SchemaWithPropertyNameOther dto1 =
        SchemaWithPropertyNameOther.newBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .setOther("other")
            .build();
    final SchemaWithPropertyNameOther dto2 =
        SchemaWithPropertyNameOther.newBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .setOther("other")
            .build();
    assertEquals(dto1, dto2);
  }

  @Test
  void equals_when_dtoHasPropertyNamedOther_then_dtoIsNotEqualsWithDifferentProperties() {
    final SchemaWithPropertyNameOther dto1 =
        SchemaWithPropertyNameOther.newBuilder()
            .andAllOptionals()
            .setFoo("foo1")
            .setOther("other")
            .build();
    final SchemaWithPropertyNameOther dto2 =
        SchemaWithPropertyNameOther.newBuilder()
            .andAllOptionals()
            .setFoo("foo2")
            .setOther("other")
            .build();
    assertNotEquals(dto1, dto2);
  }
}
