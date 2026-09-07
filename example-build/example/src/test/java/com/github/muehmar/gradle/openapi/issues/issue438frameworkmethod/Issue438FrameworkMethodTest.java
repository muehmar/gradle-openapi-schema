package com.github.muehmar.gradle.openapi.issues.issue438frameworkmethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * A property colliding with the public framework method {@code getPropertyCount()}, see issue #438.
 * The method carries the {@code @Min}/{@code @Max} constraints of {@code minProperties} / {@code
 * maxProperties} and must stay getter-shaped for bean validation to discover it, hence it is
 * renamed and the property keeps its own getter.
 */
class Issue438FrameworkMethodTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void getPropertyCount_when_propertyOfThatName_then_returnsTheProperty() throws Exception {
    final FrameworkMethodCollisionDto dto =
        MAPPER.readValue("{\"propertyCount\":\"aValue\"}", FrameworkMethodCollisionDto.class);

    assertEquals("aValue", dto.getPropertyCount());
  }

  @Test
  void getPropertyCount_underscore_when_propertyOfThatName_then_countsTheProperties()
      throws Exception {
    final FrameworkMethodCollisionDto dto =
        MAPPER.readValue("{\"propertyCount\":\"aValue\"}", FrameworkMethodCollisionDto.class);

    // The renamed framework method still counts the present properties.
    assertEquals(1, dto.getPropertyCount_());
  }

  @Test
  void serialize_when_propertyNamedAfterTheFrameworkMethod_then_propertyWritten() throws Exception {
    final FrameworkMethodCollisionDto dto =
        FrameworkMethodCollisionDto.builder().setPropertyCount("aValue").build();

    assertEquals("{\"propertyCount\":\"aValue\"}", MAPPER.writeValueAsString(dto));
  }
}
