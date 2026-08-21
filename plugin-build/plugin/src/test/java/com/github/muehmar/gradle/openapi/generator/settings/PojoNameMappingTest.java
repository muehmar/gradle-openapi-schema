package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import org.junit.jupiter.api.Test;

class PojoNameMappingTest {

  @Test
  void replaceConstant_when_constantMatches_then_replaced() {
    final PojoNameMapping mapping = PojoNameMapping.replaceConstant("User", "Person");

    final PojoName pojoName = pojoName("UserIdentifier", "Dto");

    final PojoName mappedName = mapping.map(pojoName);

    assertEquals("PersonIdentifierDto", mappedName.asString());
  }

  @Test
  void andThen_when_called_then_mappingAppliedInOrder() {
    final PojoNameMapping mapping1 = PojoNameMapping.replaceConstant("User", "Per.son");
    final PojoNameMapping mapping2 = PojoNameMapping.replaceConstant(".", "");

    final PojoNameMapping mapping = mapping1.andThen(mapping2);

    final PojoName pojoName = pojoName("User", "Dto");

    final PojoName mappedName = mapping.map(pojoName);

    assertEquals("PersonDto", mappedName.asString());
  }
}
