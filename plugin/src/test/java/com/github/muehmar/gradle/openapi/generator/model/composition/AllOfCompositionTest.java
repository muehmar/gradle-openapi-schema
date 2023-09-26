package com.github.muehmar.gradle.openapi.generator.model.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import org.junit.jupiter.api.Test;

class AllOfCompositionTest {
  @Test
  void applyMapping_when_called_then_pojoMapped() {
    final AllOfComposition allOfComposition =
        AllOfComposition.fromPojos(
            NonEmptyList.of(Pojos.objectPojo(PList.of(PojoMembers.requiredString()))));

    final AllOfComposition allOfCompositionMapped =
        allOfComposition.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "ObjectPojoMappedDto",
        allOfCompositionMapped.getPojos().head().getName().getPojoName().asString());
  }
}
