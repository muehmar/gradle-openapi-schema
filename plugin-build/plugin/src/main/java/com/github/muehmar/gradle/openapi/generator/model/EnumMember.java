package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** An enum member containing the name itself and optionally a description. */
@EqualsAndHashCode
@ToString
public class EnumMember {
  private final EnumConstantName name;
  private final String description;

  public EnumMember(EnumConstantName name, String description) {
    this.name = name;
    this.description = description;
  }

  /** */
  public static Optional<EnumMember> extractDescription(
      EnumConstantName memberName, EnumDescriptionSettings settings, String description) {
    if (settings.isDisabled()) {
      return Optional.empty();
    }

    final String prefixMatcher =
        Pattern.quote(settings.getPrefixMatcherForMember(memberName.getOriginalConstant()));
    final Pattern pattern = Pattern.compile(".*?" + prefixMatcher + "(.*)");

    return PList.fromArray(description.split("\n"))
        .map(pattern::matcher)
        .filter(Matcher::find)
        .map(matcher -> matcher.group(1))
        .headOption()
        .map(desc -> new EnumMember(memberName, desc.trim()));
  }

  /**
   * Returns a list of enums with found description. If no description is found, the description
   * will be set to an empty string in case failOnIncompleteDescriptions is turned off. In case it
   * is turned on, this method will fail if a description of a member is not found.
   */
  public static PList<EnumMember> extractDescriptions(
      PList<EnumConstantName> enumMembers, EnumDescriptionSettings settings, String description) {
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
              .filter(p -> p.first().isEmpty())
              .map(Pair::second)
              .map(EnumConstantName::getOriginalConstant)
              .mkString(", ");

      final String message =
          String.format(
              "No description found for the following enum members [%s]. "
                  + "Either complete the description for these members or turn off the "
                  + "failOnIncompleteDescriptions setting for the enum extraction.",
              missingEnumMembers);
      throw new IllegalStateException(message);
    } else {
      return enumWithDescriptions
          .zip(enumMembers)
          .map(p -> p.first().orElse(new EnumMember(p.second(), "")));
    }
  }

  public EnumConstantName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
