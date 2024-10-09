package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DiscriminatableJavaCompositionTest {

  @ParameterizedTest
  @MethodSource("compositionAndSettingsCombinations")
  void validateExactlyOneMatch_when_compositionAndSettingsCombinations_then_matchExpected(
      DiscriminatableJavaComposition composition, PojoSettings settings, boolean expected) {

    assertEquals(expected, composition.validateExactlyOneMatch(settings));
  }

  public static Stream<Arguments> compositionAndSettingsCombinations() {
    final DiscriminatableJavaComposition oneOfCompositionWithDiscriminator =
        JavaPojos.oneOfPojoWithEnumDiscriminator().getOneOfComposition().get();
    final DiscriminatableJavaComposition oneOfComposition =
        JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()).getOneOfComposition().get();
    final DiscriminatableJavaComposition anyOfPojoWithDiscriminator =
        JavaPojos.anyOfPojoWithDiscriminator().getAnyOfComposition().get();
    final DiscriminatableJavaComposition anyOfComposition =
        JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()).getAnyOfComposition().get();

    final PojoSettings defaultSettings = TestPojoSettings.defaultTestSettings();
    final PojoSettings nonStrictSettings = defaultSettings.withNonStrictOneOfValidation(true);

    return Stream.of(
        arguments(oneOfComposition, defaultSettings, true),
        arguments(oneOfComposition, nonStrictSettings, true),
        arguments(oneOfCompositionWithDiscriminator, defaultSettings, true),
        arguments(oneOfCompositionWithDiscriminator, nonStrictSettings, false),
        arguments(anyOfComposition, defaultSettings, false),
        arguments(anyOfComposition, nonStrictSettings, false),
        arguments(anyOfPojoWithDiscriminator, defaultSettings, false),
        arguments(anyOfPojoWithDiscriminator, nonStrictSettings, false));
  }
}
