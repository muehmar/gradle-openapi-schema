package com.github.muehmar.gradle.openapi.generator.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PojoName {
  private final Name name;
  private final String suffix;

  private PojoName(Name name, String suffix) {
    this.name = name;
    this.suffix = suffix;
  }

  public static PojoName ofName(Name name) {
    return new PojoName(name, "");
  }

  public static PojoName ofNameAndSuffix(Name name, String suffix) {
    return new PojoName(name, suffix);
  }

  public static PojoName ofNameAndSuffix(String name, String suffix) {
    return new PojoName(Name.ofString(name), suffix);
  }

  public static PojoName deriveOpenApiPojoName(PojoName pojoName, Name pojoMemberName) {
    final Name name = pojoName.getName().startUpperCase().append(pojoMemberName.startUpperCase());
    return new PojoName(name, pojoName.getSuffix());
  }

  public boolean equalsIgnoreCase(PojoName other) {
    return name.equalsIgnoreCase(other.name);
  }

  public PojoName startUppercase() {
    return new PojoName(name.startUpperCase(), suffix);
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
}
