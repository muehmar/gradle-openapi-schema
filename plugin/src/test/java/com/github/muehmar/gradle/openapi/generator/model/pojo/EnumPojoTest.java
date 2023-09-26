package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class EnumPojoTest {
  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final EnumPojo enumPojo =
        EnumPojo.of(componentName("Color", "Dto"), "Color", PList.of("Red", "Green", "Blue"));

    final EnumPojo enumPojoMapped = enumPojo.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals("ColorMappedDto", enumPojoMapped.getName().getPojoName().asString());
  }
}
