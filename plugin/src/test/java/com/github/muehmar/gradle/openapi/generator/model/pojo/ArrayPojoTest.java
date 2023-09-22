package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import org.junit.jupiter.api.Test;

class ArrayPojoTest {

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("Posology"), "Dto"),
            "Posology",
            NumericType.formatDouble(),
            Constraints.ofSize(Size.ofMax(4)));

    final ArrayPojo arrayPojoMapped = arrayPojo.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals("PosologyMappedDto", arrayPojoMapped.getName().asString());
  }
}
