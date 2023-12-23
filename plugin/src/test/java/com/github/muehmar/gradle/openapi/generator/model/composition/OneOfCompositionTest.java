package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.ofType;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
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
          Optional<UntypedDiscriminator> parentObjectDiscriminator,
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
    final Optional<UntypedDiscriminator> untypedDiscriminator1 =
        Optional.of(UntypedDiscriminator.fromPropertyName(requiredString().getName()));
    final Optional<UntypedDiscriminator> untypedDiscriminator2 =
        Optional.of(UntypedDiscriminator.fromPropertyName(requiredBirthdate().getName()));

    final DiscriminatorType discriminatorType =
        DiscriminatorType.fromStringType((StringType) requiredString().getType());
    final Optional<Discriminator> discriminator1 =
        untypedDiscriminator1.map(d -> Discriminator.typeDiscriminator(d, discriminatorType));

    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), requiredUsername()));
    final ObjectPojo pojo3 =
        Pojos.oneOfPojo(
            pojo1.withDiscriminator(untypedDiscriminator1),
            pojo2.withDiscriminator(untypedDiscriminator1));
    final ObjectPojo pojo4 =
        Pojos.allOfPojo(
            pojo1.withDiscriminator(untypedDiscriminator1),
            pojo2.withDiscriminator(untypedDiscriminator1));

    return Stream.of(
        arguments(NonEmptyList.of(pojo1, pojo2), Optional.empty(), Optional.empty()),
        arguments(NonEmptyList.of(pojo1, pojo2), untypedDiscriminator1, discriminator1),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(untypedDiscriminator1),
                pojo2.withDiscriminator(untypedDiscriminator2)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(untypedDiscriminator1),
                pojo2.withDiscriminator(untypedDiscriminator1)),
            Optional.empty(),
            discriminator1),
        arguments(
            NonEmptyList.of(
                pojo1.withDiscriminator(untypedDiscriminator2),
                pojo2.withDiscriminator(untypedDiscriminator2)),
            untypedDiscriminator1,
            discriminator1),
        arguments(
            NonEmptyList.of(pojo3, pojo2.withDiscriminator(untypedDiscriminator1)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(pojo3, pojo2.withDiscriminator(untypedDiscriminator1)),
            Optional.empty(),
            Optional.empty()),
        arguments(
            NonEmptyList.of(pojo4, pojo2.withDiscriminator(untypedDiscriminator1)),
            Optional.empty(),
            discriminator1));
  }

  @Test
  void determineDiscriminator_when_enumDiscriminator_then_correctDiscriminator() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Role"), PList.of("Admin", "User"));
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(ofType(enumType), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(ofType(enumType), optionalString()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final UntypedDiscriminator untypedDiscriminator =
        UntypedDiscriminator.fromPropertyName(ofType(enumType).getName());

    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    final Optional<Discriminator> discriminator =
        oneOfComposition.determineDiscriminator(Optional.of(untypedDiscriminator));

    final Discriminator expectedDiscriminator =
        Discriminator.typeDiscriminator(
            untypedDiscriminator, DiscriminatorType.fromEnumType(enumType));
    assertEquals(Optional.of(expectedDiscriminator), discriminator);
  }

  @Test
  void determineDiscriminator_when_pojo2HasNoDiscriminatorProperty_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredUsername()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<UntypedDiscriminator> discriminator =
        Optional.of(UntypedDiscriminator.fromPropertyName(requiredString().getName()));

    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    assertThrows(
        OpenApiGeneratorException.class,
        () -> oneOfComposition.determineDiscriminator(discriminator));
  }

  @Test
  void determineDiscriminator_when_discriminatorPropertyIsNotRequired_then_exception() {
    final ObjectPojo pojo1 =
        Pojos.objectPojo(PList.of(requiredString(), requiredBirthdate(), optionalNullableString()));
    final ObjectPojo pojo2 = Pojos.objectPojo(PList.of(requiredString(), optionalNullableString()));

    final NonEmptyList<Pojo> pojos = NonEmptyList.of(pojo1, pojo2);
    final Optional<UntypedDiscriminator> discriminator =
        Optional.of(UntypedDiscriminator.fromPropertyName(optionalNullableString().getName()));

    final OneOfComposition oneOfComposition = new OneOfComposition(pojos);

    // Method call
    assertThrows(
        OpenApiGeneratorException.class,
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
