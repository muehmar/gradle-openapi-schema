package com.github.muehmar.gradle.openapi.generator.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import org.junit.jupiter.api.Test;

class MapTypeTest {
  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final MapType mapType =
        MapType.ofKeyAndValueType(
            StandardObjectType.ofName(pojoName("KeyType", "Dto")),
            StandardObjectType.ofName(pojoName("ValueType", "Dto")));

    final MapType mapTypeMapped = mapType.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "KeyTypeMappedDto",
        mapTypeMapped
            .getKey()
            .asObjectType()
            .map(ObjectType::getName)
            .map(PojoName::asString)
            .orElse(""));
    assertEquals(
        "ValueTypeMappedDto",
        mapTypeMapped
            .getValue()
            .asObjectType()
            .map(ObjectType::getName)
            .map(PojoName::asString)
            .orElse(""));
  }
}
