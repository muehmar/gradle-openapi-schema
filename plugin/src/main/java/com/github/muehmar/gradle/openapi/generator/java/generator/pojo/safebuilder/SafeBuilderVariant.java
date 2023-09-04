package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

public enum SafeBuilderVariant {
  STANDARD(""),
  FULL("Full");

  private final String builderNamePrefix;

  SafeBuilderVariant(String builderNamePrefix) {
    this.builderNamePrefix = builderNamePrefix;
  }

  public String getBuilderNamePrefix() {
    return builderNamePrefix;
  }
}
