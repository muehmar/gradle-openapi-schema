package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OneOfCompositionTest {

  @Test
  void new_when_everythingOk_then_created() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), requiredUsername()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<Discriminator> discriminator =
        Optional.of(Discriminator.fromPropertyName(requiredString().getName()));

    // Method call
    final OneOfComposition oneOfComposition =
        assertDoesNotThrow(() -> new OneOfComposition(pojos, discriminator));

    assertEquals(discriminator, oneOfComposition.getDiscriminator());
    assertEquals(pojos, oneOfComposition.getPojos());
  }

  @Test
  void new_when_pojo2HasNoDiscriminatorProperty_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredUsername()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<Discriminator> discriminator =
        Optional.of(Discriminator.fromPropertyName(requiredString().getName()));

    // Method call
    assertThrows(IllegalArgumentException.class, () -> new OneOfComposition(pojos, discriminator));
  }

  @Test
  void new_when_discriminatorPropertyIsNotRequired_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), optionalNullableString()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<Discriminator> discriminator =
        Optional.of(Discriminator.fromPropertyName(optionalNullableString().getName()));

    // Method call
    assertThrows(IllegalArgumentException.class, () -> new OneOfComposition(pojos, discriminator));
  }

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
