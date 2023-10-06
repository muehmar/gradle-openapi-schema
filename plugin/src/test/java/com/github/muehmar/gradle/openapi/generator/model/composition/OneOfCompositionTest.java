package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OneOfCompositionTest {

  @ParameterizedTest
  @MethodSource("pojosAndParentObjectDiscriminator")
  void
      determineDiscriminator_when_pojosAndParentObjectDiscriminator_then_matchExpectedDiscriminator(
          NonEmptyList<Pojo> pojos,
          Optional<Discriminator> parentObjectDiscriminator,
          Optional<Discriminator> expectedDiscriminator) {
    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    final Optional<Discriminator> determinedDiscriminator =
        assertDoesNotThrow(
            () -> oneOfComposition.determineDiscriminator(parentObjectDiscriminator));

    assertEquals(expectedDiscriminator, determinedDiscriminator);
    assertEquals(pojos, oneOfComposition.getPojos());
  }

  public static Stream<Arguments> pojosAndParentObjectDiscriminator() {
    final Optional<Discriminator> discriminator1 =
        Optional.of(Discriminator.fromPropertyName(requiredString().getName()));
    final Optional<Discriminator> discriminator2 =
        Optional.of(Discriminator.fromPropertyName(requiredBirthdate().getName()));

    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), requiredUsername()));
    final ObjectPojo pojo3 =
        Pojos.oneOfPojo(
            pojo1.withDiscriminator(discriminator1), pojo2.withDiscriminator(discriminator1));
    final ObjectPojo pojo4 =
        Pojos.allOfPojo(
            pojo1.withDiscriminator(discriminator1), pojo2.withDiscriminator(discriminator1));

    return Stream.of(
        arguments(NonEmptyList.of(pojo1, pojo2), Optional.empty(), Optional.empty()),
        arguments(NonEmptyList.of(pojo1, pojo2), discriminator1, discriminator1),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(discriminator1), pojo2.withDiscriminator(discriminator2)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(discriminator1), pojo2.withDiscriminator(discriminator1)),
            Optional.empty(),
            discriminator1),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(discriminator2), pojo2.withDiscriminator(discriminator2)),
            discriminator1,
            discriminator1),
        arguments(
            NonEmptyList.of(pojo3, pojo2.withDiscriminator(discriminator1)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(pojo3, pojo2.withDiscriminator(discriminator1)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(pojo4, pojo2.withDiscriminator(discriminator1)),
            Optional.empty(),
            discriminator1));
  }

  @Test
  void determineDiscriminator_when_pojo2HasNoDiscriminatorProperty_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredUsername()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<Discriminator> discriminator =
        Optional.of(Discriminator.fromPropertyName(requiredString().getName()));

    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    assertThrows(
        IllegalArgumentException.class,
        () -> oneOfComposition.determineDiscriminator(discriminator));
  }

  @Test
  void determineDiscriminator_when_discriminatorPropertyIsNotRequired_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), optionalNullableString()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<Discriminator> discriminator =
        Optional.of(Discriminator.fromPropertyName(optionalNullableString().getName()));

    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    assertThrows(
        IllegalArgumentException.class,
        () -> oneOfComposition.determineDiscriminator(discriminator));
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
