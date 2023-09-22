package com.github.muehmar.gradle.openapi.generator.model.composition;

import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import org.junit.jupiter.api.Test;

class AnyOfCompositionTest {
  @Test
  void applyMapping_when_called_then_pojoMapped() {
    final AnyOfComposition anyOfComposition =
        AnyOfComposition.fromPojos(
            NonEmptyList.of(Pojos.objectPojo(PList.of(PojoMembers.requiredString()))));

    final AnyOfComposition anyOfCompositionMapped =
        anyOfComposition.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "ObjectPojoMappedDto", anyOfCompositionMapped.getPojos().head().getName().asString());
  }
}
