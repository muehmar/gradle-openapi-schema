package com.github.muehmar.gradle.openapi.generator.model;

import java.util.Objects;

/** A name which includes a suffix */
public class SuffixedName {
  private final Name name;
  private final String suffix;

  private SuffixedName(Name name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static SuffixedName ofName(Name name) {
    return new SuffixedName(name, "");
  }

  public static SuffixedName ofNameAndSuffix(Name name, String suffix) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(suffix);
    return new SuffixedName(name, suffix);
  }

  public static SuffixedName deriveOpenApiPojoName(SuffixedName pojoName, Name pojoMemberName) {
    return pojoName
        .getName()
        .startUpperCase()
        .append(pojoMemberName.startUpperCase())
        .suffix(pojoName.getSuffix());
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
    return asString();
  }
}
