package com.github.muehmar.gradle.openapi.generator.model.type;

import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ArrayType arrayType =
        ArrayType.ofItemType(ObjectType.ofName(PojoName.ofNameAndSuffix("Posology", "Dto")));

    final ArrayType arrayTypeMapped = arrayType.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "PosologyMappedDto",
        arrayTypeMapped
            .getItemType()
            .asObjectType()
            .map(ObjectType::getName)
            .map(PojoName::asString)
            .orElse(""));
  }
}
