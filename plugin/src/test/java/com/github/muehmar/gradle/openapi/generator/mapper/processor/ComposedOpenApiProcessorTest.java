package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import org.junit.jupiter.api.Test;

class ComposedOpenApiProcessorTest extends ResourceSchemaOpenApiTest {
  @Test
  void fromSchema_when_allof_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos = processSchema("/schemas/compositions", "allof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("ColorDto", "ExtendedColorAllOfDto", "ExtendedColorDto"),
        pojos.map(Pojo::getName).map(PojoName::asString));

    // ColorDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo colorDto = ((ObjectPojo) pojos.apply(0));

    final PList<PojoMember> colorMembers = colorDto.getMembers();
    assertEquals(2, colorMembers.size());
    assertEquals(
        PList.of("colorKey", "colorName"),
        colorMembers.map(PojoMember::getName).map(Name::asString));

    // ExtendedColorAllOfDto
    assertTrue(pojos.apply(1) instanceof ObjectPojo);
    final ObjectPojo extendedColorAllOfDto = ((ObjectPojo) pojos.apply(1));

    final PList<PojoMember> extendedColorAllOfMembers = extendedColorAllOfDto.getMembers();
    assertEquals(2, extendedColorAllOfMembers.size());
    assertEquals(
        PList.of("description", "transparency"),
        extendedColorAllOfMembers.map(PojoMember::getName).map(Name::asString));

    // ExtendedColorDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo extendedColorDto = ((ObjectPojo) pojos.apply(2));

    final PList<PojoMember> extendedColorDtoMembers = extendedColorDto.getMembers();
    assertEquals(4, extendedColorDtoMembers.size());
    assertEquals(
        PList.of("colorKey", "colorName", "description", "transparency"),
        extendedColorDtoMembers.map(PojoMember::getName).map(Name::asString));
  }
}
