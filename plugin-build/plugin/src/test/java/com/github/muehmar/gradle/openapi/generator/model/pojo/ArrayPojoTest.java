package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import org.junit.jupiter.api.Test;

class ArrayPojoTest {

  @Test
  void replaceObjectType_when_matchesItemObjectType_then_itemTypeReplacedCorrectly() {
    final PojoName objectTypeName = PojoName.ofNameAndSuffix("PosologyItem", "Dto");

    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            componentName("Posology", "Dto"),
            "Posology",
            Nullability.NOT_NULLABLE,
            StandardObjectType.ofName(objectTypeName),
            Constraints.ofSize(Size.ofMax(4)));

    final ArrayPojo adjustedArrayPojo =
        arrayPojo.replaceObjectType(objectTypeName, "Posology item", NumericType.formatDouble());

    assertEquals(NumericType.formatDouble(), adjustedArrayPojo.getItemType());
  }

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            componentName("Posology", "Dto"),
            "Posology",
            Nullability.NOT_NULLABLE,
            NumericType.formatDouble(),
            Constraints.ofSize(Size.ofMax(4)));

    final ArrayPojo arrayPojoMapped = arrayPojo.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals("PosologyMappedDto", arrayPojoMapped.getName().getPojoName().asString());
  }
}
