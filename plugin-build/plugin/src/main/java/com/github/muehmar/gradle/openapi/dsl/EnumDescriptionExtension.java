package com.github.muehmar.gradle.openapi.dsl;

import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import java.io.Serializable;
import java.util.Objects;
import javax.inject.Inject;

public class EnumDescriptionExtension implements Serializable {
  private boolean enabled;
  private String prefixMatcher;
  private boolean failOnIncompleteDescriptions;

  @Inject
  public EnumDescriptionExtension() {
    this.enabled = false;
    this.prefixMatcher = "";
    this.failOnIncompleteDescriptions = false;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setPrefixMatcher(String prefixMatcher) {
    this.prefixMatcher = prefixMatcher;
  }

  public void setFailOnIncompleteDescriptions(boolean failOnIncompleteDescriptions) {
    this.failOnIncompleteDescriptions = failOnIncompleteDescriptions;
  }

  public EnumDescriptionSettings toEnumDescriptionSettings() {
    return new EnumDescriptionSettings(enabled, prefixMatcher, failOnIncompleteDescriptions);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EnumDescriptionExtension that = (EnumDescriptionExtension) o;
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
    return "EnumDescriptionExtraction{"
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
