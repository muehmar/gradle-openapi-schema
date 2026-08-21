package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import org.junit.jupiter.api.Test;

class PropertyInfoNameTest {

  @Test
  void asString_when_calledWithPojoNameAndMemberName_then_correctString() {
    final JavaPojoName javaPojoName =
        JavaPojoName.fromPojoName(PojoName.ofNameAndSuffix("Schema?Object", "Dto"));
    final JavaName memberName = JavaName.fromString("property?Member");

    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(javaPojoName, memberName);

    assertEquals("Schema_ObjectDto.property?Member", propertyInfoName.asString());
  }
}
