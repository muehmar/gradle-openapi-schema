package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ResourceComposedPojoMapperTest extends ResourceSchemaMapperTest {
  @Test
  void map_when_allOf_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos = mapSchema("/schemas/compositions", "allof.yml");

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

  @Test
  void map_when_oneOf_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos = mapSchema("/schemas/compositions", "oneof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ComposedPojo);
    final ComposedPojo personDto = ((ComposedPojo) pojos.apply(1));

    final PList<Pojo> personPojos = personDto.getPojos();
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);
    assertEquals(ComposedPojo.CompositionType.ONE_OF, personDto.getCompositionType());
    assertEquals(Optional.empty(), personDto.getDiscriminator());
  }

  @Test
  void map_when_oneOfWithDiscriminator_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos = mapSchema("/schemas/compositions", "oneof_discriminator.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ComposedPojo);
    final ComposedPojo personDto = ((ComposedPojo) pojos.apply(1));

    final PList<Pojo> personPojos = personDto.getPojos();
    assertEquals(2, personPojos.size());
    assertEquals(ComposedPojo.CompositionType.ONE_OF, personDto.getCompositionType());
    assertEquals(PList.of(adminDto, userDto), personPojos);

    final Discriminator expectedDiscriminator =
        Discriminator.fromPropertyName(Name.ofString("personType"));
    assertEquals(Optional.of(expectedDiscriminator), personDto.getDiscriminator());
  }

  @Test
  void map_when_oneOfWithDiscriminatorAndMapping_then_allPojosCorrectMapped() {
    final PList<Pojo> pojos =
        mapSchema("/schemas/compositions", "oneof_discriminatorWithMapping.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ComposedPojo);
    final ComposedPojo personDto = ((ComposedPojo) pojos.apply(1));

    final PList<Pojo> personPojos = personDto.getPojos();
    assertEquals(2, personPojos.size());
    assertEquals(ComposedPojo.CompositionType.ONE_OF, personDto.getCompositionType());
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
    final PList<Pojo> pojos = mapSchema("/schemas", "compositions/anyof.yml");

    assertEquals(3, pojos.size());
    assertEquals(
        PList.of("AdminDto", "PersonDto", "UserDto"),
        pojos.map(Pojo::getName).map(PojoName::asString));

    // AdminDto
    assertTrue(pojos.apply(0) instanceof ObjectPojo);
    final ObjectPojo adminDto = ((ObjectPojo) pojos.apply(0));

    // UserDto
    assertTrue(pojos.apply(2) instanceof ObjectPojo);
    final ObjectPojo userDto = ((ObjectPojo) pojos.apply(2));

    // PersonDto
    assertTrue(pojos.apply(1) instanceof ComposedPojo);
    final ComposedPojo personDto = ((ComposedPojo) pojos.apply(1));

    final PList<Pojo> personPojos = personDto.getPojos();
    assertEquals(2, personPojos.size());
    assertEquals(PList.of(adminDto, userDto), personPojos);
    assertEquals(ComposedPojo.CompositionType.ANY_OF, personDto.getCompositionType());
    assertEquals(Optional.empty(), personDto.getDiscriminator());
  }
}
