package com.github.muehmar.gradle.openapi.generator.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import org.junit.jupiter.api.Test;

class ArrayTypeTest {

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ArrayType arrayType =
        ArrayType.ofItemType(ObjectType.ofName(pojoName("Posology", "Dto")), NOT_NULLABLE);

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
