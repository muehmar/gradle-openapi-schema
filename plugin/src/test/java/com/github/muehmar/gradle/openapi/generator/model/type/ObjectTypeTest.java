package com.github.muehmar.gradle.openapi.generator.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import org.junit.jupiter.api.Test;

class ObjectTypeTest {
  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ObjectType objectType = ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"));

    final ObjectType objectTypeMapped =
        objectType.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals("ObjectMappedDto", objectTypeMapped.getName().asString());
  }
}
