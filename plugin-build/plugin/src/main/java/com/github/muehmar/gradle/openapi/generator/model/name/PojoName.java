package com.github.muehmar.gradle.openapi.generator.model.name;

import lombok.EqualsAndHashCode;

/** Represents the name of a pojo. */
@EqualsAndHashCode
public class PojoName {
  private final Name name;
  private final String suffix;

  PojoName(Name name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static PojoName ofName(Name name) {
    return new PojoName(name.startUpperCase(), "");
  }

  public static PojoName ofNameAndSuffix(Name name, String suffix) {
    return new PojoName(name.startUpperCase(), suffix);
  }

  public static PojoName ofNameAndSuffix(String name, String suffix) {
    return new PojoName(Name.ofString(name).startUpperCase(), suffix);
  }

  public PojoName deriveMemberSchemaName(Name pojoMemberName) {
    final Name derivedName = name.startUpperCase().append(pojoMemberName.startUpperCase());
    return new PojoName(derivedName, suffix);
  }

  public PojoName appendToName(String append) {
    return new PojoName(name.append(append), suffix);
  }

  public PojoName prependSuffix(String prependText) {
    return new PojoName(name, prependText + suffix);
  }

  public boolean equalsIgnoreCase(PojoName other) {
    return name.equalsIgnoreCase(other.name) && suffix.equals(other.suffix);
  }

  public Name getName() {
    return name;
  }

  public String getSuffix() {
    return suffix;
  }

  public String asString() {
    return name.append(suffix).asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
