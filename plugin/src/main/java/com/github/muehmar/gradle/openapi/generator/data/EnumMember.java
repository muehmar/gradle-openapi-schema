package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An enum member containing the name itself and optionally a description. */
public class EnumMember {
  private final Name name;
  private final String description;

  public EnumMember(Name name, String description) {
    this.name = name;
    this.description = description;
  }

  /** */
  public static Optional<EnumMember> extractDescription(
      Name memberName, EnumDescriptionSettings settings, String description) {
    if (settings.isDisabled()) {
      return Optional.empty();
    }

    final String prefixMatcher = settings.getPrefixMatcherForMember(memberName);
    final Pattern pattern = Pattern.compile(".*" + prefixMatcher + "(.*)");

    return PList.fromArray(description.split("\n"))
        .map(pattern::matcher)
        .filter(Matcher::find)
        .map(matcher -> matcher.group(1))
        .headOption()
        .map(desc -> new EnumMember(memberName, desc.trim()));
  }

  /**
   * Returns a list of enums with found description. If no description is found, the description
   * will be set to an empty string in case failOnMissingDescription is turned off. In case it is
   * turned on, this method will fail if a description of a member is not found.
   */
  public static PList<EnumMember> extractDescriptions(
      PList<Name> enumMembers, EnumDescriptionSettings settings, String description) {
    if (settings.isDisabled()) {
      return enumMembers.map(name -> new EnumMember(name, ""));
    }

    final PList<Optional<EnumMember>> enumWithDescriptions =
        enumMembers.map(m -> extractDescription(m, settings, description));

    int countOfMembersWithDescription = enumWithDescriptions.flatMapOptional(e -> e).size();
    final boolean incompleteDescription =
        countOfMembersWithDescription != enumMembers.size() && countOfMembersWithDescription != 0;

    if (settings.isFailOnIncompleteDescriptions() && incompleteDescription) {
      final String missingEnumMembers =
          enumWithDescriptions
              .zip(enumMembers)
              .filter(p -> !p.first().isPresent())
              .map(Pair::second)
              .map(Name::asString)
              .mkString(", ");

      final String message =
          String.format(
              "No description found for the following enum members [%s]. "
                  + "Either complete the description for these members or turn off the "
                  + "failOnMissingDescription setting for the enum extraction.",
              missingEnumMembers);
      throw new IllegalStateException(message);
    } else {
      return enumWithDescriptions
          .zip(enumMembers)
          .map(p -> p.first().orElse(new EnumMember(p.second(), "")));
    }
  }

  public Name getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnumMember that = (EnumMember) o;
    return Objects.equals(name, that.name) && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  public String toString() {
    return "EnumMember{" + "name=" + name + ", description='" + description + '\'' + '}';
  }
}
