package com.github.muehmar.gradle.openapi.generator.config;

import java.util.Objects;

public class GeneratorConfig {
  private final String packageName;
  private final String suffix;

  public GeneratorConfig(String packageName, String suffix) {
    this.packageName = packageName;
    this.suffix = suffix;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getSuffix() {
    return suffix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneratorConfig that = (GeneratorConfig) o;
    return Objects.equals(packageName, that.packageName) && Objects.equals(suffix, that.suffix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageName, suffix);
  }

  @Override
  public String toString() {
    return "GeneratorConfig{"
        + "packageName='"
        + packageName
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + '}';
  }
}
