package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaName {
  private final Name name;

  private JavaName(Name name) {
    this.name = name;
  }

  public static JavaName fromName(Name name) {
    return new JavaName(name);
  }

  public static JavaName fromString(String name) {
    return new JavaName(Name.ofString(name));
  }

  public JavaName prefix(String prefix) {
    return new JavaName(name.prefix(prefix));
  }

  public JavaName append(String append) {
    return new JavaName(name.append(append));
  }

  public JavaName startUpperCase() {
    return new JavaName(name.startUpperCase());
  }

  public JavaIdentifier asIdentifier() {
    return JavaIdentifier.fromName(name);
  }

  public Name asName() {
    return name;
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
