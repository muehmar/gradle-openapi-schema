package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ResourceComposedPojoMapperTest {
  @Test
  void map_when_allOf_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/schemas/compositions", "allof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("ColorDto", "ExtendedColorAllOfDto", "ExtendedColorDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));

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

    final Optional<AllOfComposition> allOfComposition = extendedColorDto.getAllOfComposition();
    assertTrue(allOfComposition.isPresent());
    assertEquals(
        PList.of(colorDto, extendedColorAllOfDto),
        extendedColorDto
            .getAllOfComposition()
            .map(AllOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty));
  }

  @Test
  void map_when_oneOf_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/schemas/compositions", "oneof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ObjectPojo);
    final ObjectPojo personDto = ((ObjectPojo) pojos.apply(1));

    final PList<Pojo> personPojos =
        personDto
            .getOneOfComposition()
            .map(OneOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty);
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);
  }

  @Test
  void map_when_oneOfWithDiscriminator_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/schemas/compositions", "oneof_discriminator.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ObjectPojo);
    final ObjectPojo personDto = ((ObjectPojo) pojos.apply(1));

    final PList<Pojo> personPojos =
        personDto
            .getOneOfComposition()
            .map(OneOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty);
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);

    final Discriminator expectedDiscriminator =
        Discriminator.fromPropertyName(Name.ofString("personType"));
    assertEquals(Optional.of(expectedDiscriminator), personDto.getDiscriminator());
  }

  @Test
  void map_when_oneOfWithDiscriminatorAndMapping_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema(
            "/schemas/compositions", "oneof_discriminatorWithMapping.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ObjectPojo);
    final ObjectPojo personDto = ((ObjectPojo) pojos.apply(1));

    final PList<Pojo> personPojos =
        personDto
            .getOneOfComposition()
            .map(OneOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty);
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);

    final Map<String, Name> mapping = new HashMap<>();
    mapping.put("usr", Name.ofString("User"));
    mapping.put("adm", Name.ofString("Admin"));
    final Discriminator expectedDiscriminator =
        Discriminator.fromPropertyNameAndMapping(Name.ofString("personType"), mapping);
    assertEquals(Optional.of(expectedDiscriminator), personDto.getDiscriminator());
  }

  @Test
  void map_when_anyOf_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        ResourceSchemaMappingTestUtil.mapSchema("/schemas", "compositions/anyof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(ComponentName::getPojoName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ObjectPojo);
    final ObjectPojo personDto = ((ObjectPojo) pojos.apply(1));

    final PList<Pojo> personPojos =
        personDto
            .getAnyOfComposition()
            .map(AnyOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElseGet(PList::empty);
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);
  }
}
