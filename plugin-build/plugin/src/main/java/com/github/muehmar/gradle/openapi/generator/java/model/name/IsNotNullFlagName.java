package com.github.muehmar.gradle.openapi.generator.java.model.name;

import lombok.Value;

@Value
public class IsNotNullFlagName {
  JavaName name;

  private IsNotNullFlagName(JavaName name) {
    this.name = name;
  }

  public static IsNotNullFlagName fromName(JavaName name) {
    return new IsNotNullFlagName(name.startUpperCase().prefix("is").append("NotNull"));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
