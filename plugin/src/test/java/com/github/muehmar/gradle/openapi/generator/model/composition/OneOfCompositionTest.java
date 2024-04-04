package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import org.junit.jupiter.api.Test;

class OneOfCompositionTest {

  @Test
  void applyMapping_when_called_then_nameMappedCorrectly() {
    final OneOfComposition oneOfComposition =
        OneOfComposition.fromPojos(
            NonEmptyList.of(
                Pojos.objectPojo(PList.of(requiredString())),
                Pojos.objectPojo(PList.of(requiredString()))));

    final OneOfComposition oneOfCompositionMapped =
        oneOfComposition.applyMapping(name -> name.appendToName("Mapped"));

    assertEquals(
        "ObjectPojoMappedDto",
        oneOfCompositionMapped.getPojos().head().getName().getPojoName().asString());
  }
}
