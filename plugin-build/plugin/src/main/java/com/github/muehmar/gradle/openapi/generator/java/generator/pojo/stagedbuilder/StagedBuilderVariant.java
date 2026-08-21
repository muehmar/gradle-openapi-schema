package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder;

public enum StagedBuilderVariant {
  STANDARD(""),
  FULL("Full");

  private final String builderNamePrefix;

  StagedBuilderVariant(String builderNamePrefix) {
    this.builderNamePrefix = builderNamePrefix;
  }

  public String getBuilderNamePrefix() {
    return builderNamePrefix;
  }
}
