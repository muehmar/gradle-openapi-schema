package com.github.muehmar.gradle.openapi.generator.settings;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import java.io.Serializable;
import java.util.Objects;

/** Settings how and if the description of enum should get extracted. */
public class EnumDescriptionSettings implements Serializable {
  public static final String ENUM_PLACEHOLDER = "__ENUM__";
  private final boolean enabled;
  private final String prefixMatcher;
  private final boolean failOnIncompleteDescriptions;

  public EnumDescriptionSettings(
      boolean enabled, String prefixMatcher, boolean failOnIncompleteDescriptions) {
    if (enabled && !prefixMatcher.contains(ENUM_PLACEHOLDER)) {
      throw new IllegalArgumentException(
          "Prefix must contain the enum placeholder '"
              + ENUM_PLACEHOLDER
              + "' to properly match the correct description. But was '"
              + prefixMatcher
              + "'");
    }

    this.enabled = enabled;
    this.prefixMatcher = prefixMatcher;
    this.failOnIncompleteDescriptions = failOnIncompleteDescriptions;
  }

  public static EnumDescriptionSettings disabled() {
    return new EnumDescriptionSettings(false, "", false);
  }

  public static EnumDescriptionSettings enabled(
      String prefixMatcher, boolean failOnIncompleteDescriptions) {
    return new EnumDescriptionSettings(true, prefixMatcher, failOnIncompleteDescriptions);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isDisabled() {
    return !isEnabled();
  }

  public String getPrefixMatcher() {
    return prefixMatcher;
  }

  public String getPrefixMatcherForMember(Name member) {
    return getPrefixMatcher().replace(ENUM_PLACEHOLDER, member.asString());
  }

  public boolean isFailOnIncompleteDescriptions() {
    return failOnIncompleteDescriptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnumDescriptionSettings that = (EnumDescriptionSettings) o;
    return enabled == that.enabled
        && failOnIncompleteDescriptions == that.failOnIncompleteDescriptions
        && Objects.equals(prefixMatcher, that.prefixMatcher);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled, prefixMatcher, failOnIncompleteDescriptions);
  }

  @Override
  public String toString() {
    return "EnumDescriptionSettings{"
        + "enabled="
        + enabled
        + ", prefixMatcher='"
        + prefixMatcher
        + '\''
        + ", failOnIncompleteDescriptions="
        + failOnIncompleteDescriptions
        + '}';
  }
}
