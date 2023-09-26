package com.github.muehmar.gradle.openapi.generator.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ObjectTypeTest {
  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ObjectType objectType = ObjectType.ofName(pojoName("Object", "Dto"));

    final ObjectType objectTypeMapped =
        objectType.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals("ObjectMappedDto", objectTypeMapped.getName().asString());
  }
}
