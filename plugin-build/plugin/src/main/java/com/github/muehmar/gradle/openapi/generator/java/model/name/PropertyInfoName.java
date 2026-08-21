package com.github.muehmar.gradle.openapi.generator.java.model.name;

import lombok.EqualsAndHashCode;

/**
 * This name describes a property which is intended to be used as information within any message of
 * a warning or exception. It consists of the generated DTO class and the name of the property in
 * the specification.
 */
@EqualsAndHashCode
public class PropertyInfoName {
  private final String name;

  private PropertyInfoName(String name) {
    this.name = name;
  }

  public static PropertyInfoName fromPojoNameAndMemberName(
      JavaPojoName pojoName, JavaName memberName) {
    return new PropertyInfoName(String.format("%s.%s", pojoName, memberName.getOriginalName()));
  }

  public String asString() {
    return name;
  }

  @Override
  public String toString() {
    return asString();
  }
}
