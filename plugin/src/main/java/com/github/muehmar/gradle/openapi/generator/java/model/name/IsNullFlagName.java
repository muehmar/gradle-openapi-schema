package com.github.muehmar.gradle.openapi.generator.java.model.name;

import lombok.Value;

@Value
public class IsNullFlagName {
  JavaName name;

  private IsNullFlagName(JavaName name) {
    this.name = name;
  }

  public static IsNullFlagName fromName(JavaName name) {
    return new IsNullFlagName(name.startUpperCase().prefix("is").append("Null"));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
