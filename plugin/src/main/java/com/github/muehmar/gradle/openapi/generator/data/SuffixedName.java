package com.github.muehmar.gradle.openapi.generator.data;

import java.util.Objects;

/** A name which includes a suffix */
public class SuffixedName {
  private final Name name;
  private final String suffix;

  private SuffixedName(Name name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static SuffixedName of(Name name, String suffix) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(suffix);
    return new SuffixedName(name, suffix);
  }

  public Name getName() {
    return name;
  }

  public String getSuffix() {
    return suffix;
  }

  public String asString() {
    return name.asString() + suffix;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SuffixedName that = (SuffixedName) o;
    return Objects.equals(name, that.name) && Objects.equals(suffix, that.suffix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, suffix);
  }

  @Override
  public String toString() {
    return "SuffixedName{" + "name=" + name + ", suffix='" + suffix + '\'' + '}';
  }
}
