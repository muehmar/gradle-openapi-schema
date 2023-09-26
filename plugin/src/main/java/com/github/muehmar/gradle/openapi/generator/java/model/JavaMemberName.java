package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaMemberName {
  private final Name name;

  private JavaMemberName(Name name) {
    this.name = name;
  }

  public static JavaMemberName wrap(Name name) {
    return new JavaMemberName(name);
  }

  public JavaName asJavaName() {
    return JavaName.fromName(name);
  }

  public JavaIdentifier asIdentifier() {
    return asJavaName().asIdentifier();
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
