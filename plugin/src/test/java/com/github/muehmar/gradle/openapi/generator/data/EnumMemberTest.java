package com.github.muehmar.gradle.openapi.generator.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EnumMemberTest {
  @ParameterizedTest
  @MethodSource("enumDescriptionExtractionArguments")
  void extractDescription_when_differentEnumsAsInput_then_correspondingDescriptionExtracted(
      String memberName, Optional<String> foundDescription) {
    final String input =
        "* `GREEN`: This is for green\n"
            + "* `RED`: This is for red\n"
            + "* `BLUE`: This is for blue";

    final Name memberNames = Name.of(memberName);
    final EnumDescriptionSettings settings = EnumDescriptionSettings.enabled("`__ENUM__`:", false);

    final Optional<EnumMember> enumMember =
        EnumMember.extractDescription(memberNames, settings, input);

    assertEquals(foundDescription, enumMember.map(EnumMember::getDescription));
    assertEquals(
        Optional.of(memberNames).filter(ign -> foundDescription.isPresent()),
        enumMember.map(EnumMember::getName));
  }

  private static Stream<Arguments> enumDescriptionExtractionArguments() {
    return Stream.of(
        Arguments.of("RED", Optional.of("This is for red")),
        Arguments.of("GREEN", Optional.of("This is for green")),
        Arguments.of("YELLOW", Optional.empty()),
        Arguments.of("BLUE", Optional.of("This is for blue")));
  }

  @ParameterizedTest
  @MethodSource("enumDescriptionExtractionArguments")
  void
      extractDescription_when_prefixMatcherWithSpecialCharacters_then_quotedAndCorrectDescriptionExtracted(
          String memberName, Optional<String> foundDescription) {
    final String input =
        "* `GREEN`:.* This is for green\n"
            + "* `RED`:.* This is for red\n"
            + "* `BLUE`:.* This is for blue";

    final Name memberNames = Name.of(memberName);
    final EnumDescriptionSettings settings =
        EnumDescriptionSettings.enabled("`__ENUM__`:.*", false);

    final Optional<EnumMember> enumMember =
        EnumMember.extractDescription(memberNames, settings, input);

    assertEquals(foundDescription, enumMember.map(EnumMember::getDescription));
    assertEquals(
        Optional.of(memberNames).filter(ign -> foundDescription.isPresent()),
        enumMember.map(EnumMember::getName));
  }

  @Test
  void
      extractDescriptions_when_notForEveryMemberADescriptionIsPresentAndDontFailOnIncompleteDescription_then_partiallyExtracted() {
    final String input =
        "* `GREEN`: This is for green\n"
            + "* `RED`: This is for red\n"
            + "* `BLUE`: This is for blue";

    final PList<Name> memberNames = PList.of(Name.of("RED"), Name.of("GREEN"), Name.of("YELLOW"));
    final EnumDescriptionSettings settings = EnumDescriptionSettings.enabled("`__ENUM__`:", false);
    final PList<EnumMember> enumMembers =
        EnumMember.extractDescriptions(memberNames, settings, input);

    final PList<EnumMember> expected =
        PList.of(
            new EnumMember(memberNames.apply(0), "This is for red"),
            new EnumMember(memberNames.apply(1), "This is for green"),
            new EnumMember(memberNames.apply(2), ""));

    assertEquals(expected.size(), enumMembers.size());
    assertEquals(expected, enumMembers);
  }

  @Test
  void
      extractDescriptions_when_notForEveryMemberADescriptionIsPresentAndFailOnIncompleteDescription_then_exceptionThrown() {
    final String input =
        "* `GREEN`: This is for green\n"
            + "* `RED`: This is for red\n"
            + "* `BLUE`: This is for blue";

    final PList<Name> memberNames = PList.of(Name.of("RED"), Name.of("GREEN"), Name.of("YELLOW"));
    final EnumDescriptionSettings settings = EnumDescriptionSettings.enabled("`__ENUM__`:", true);
    assertThrows(
        IllegalStateException.class,
        () -> EnumMember.extractDescriptions(memberNames, settings, input));
  }

  @Test
  void
      extractDescriptions_when_noDescriptionPresentAndFailOnIncompleteDescription_then_noExceptionThrown() {
    final String input =
        "* `GREEN`: This is for green\n"
            + "* `RED`: This is for red\n"
            + "* `BLUE`: This is for blue";

    final PList<Name> memberNames =
        PList.of(Name.of("YELLOW"), Name.of("ORANGE"), Name.of("BROWN"));
    final EnumDescriptionSettings settings = EnumDescriptionSettings.enabled("`__ENUM__`:", true);
    final PList<EnumMember> enumMembers =
        EnumMember.extractDescriptions(memberNames, settings, input);

    final PList<EnumMember> expected =
        PList.of(
            new EnumMember(memberNames.apply(0), ""),
            new EnumMember(memberNames.apply(1), ""),
            new EnumMember(memberNames.apply(2), ""));

    assertEquals(expected.size(), enumMembers.size());
    assertEquals(expected, enumMembers);
  }

  @Test
  void
      extractDescriptions_when_forAllMembersADescriptionIsPresentAndFailOnIncompleteDescription_then_extractedSuccessfully() {
    final String input =
        "* `GREEN`: This is for green\n"
            + "* `RED`: This is for red\n"
            + "* `BLUE`: This is for blue";

    final PList<Name> memberNames = PList.of(Name.of("BLUE"), Name.of("RED"), Name.of("GREEN"));
    final EnumDescriptionSettings settings = EnumDescriptionSettings.enabled("`__ENUM__`:", true);
    final PList<EnumMember> enumMembers =
        EnumMember.extractDescriptions(memberNames, settings, input);

    final PList<EnumMember> expected =
        PList.of(
            new EnumMember(memberNames.apply(0), "This is for blue"),
            new EnumMember(memberNames.apply(1), "This is for red"),
            new EnumMember(memberNames.apply(2), "This is for green"));

    assertEquals(expected.size(), enumMembers.size());
    assertEquals(expected, enumMembers);
  }
}
