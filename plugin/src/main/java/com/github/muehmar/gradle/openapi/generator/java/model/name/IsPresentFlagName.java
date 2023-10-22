package com.github.muehmar.gradle.openapi.generator.java.model.name;

import lombok.Value;

@Value
public class IsPresentFlagName {
  JavaName name;

  private IsPresentFlagName(JavaName name) {
    this.name = name;
  }

  public static IsPresentFlagName fromName(JavaName name) {
    return new IsPresentFlagName(name.startUpperCase().prefix("is").append("Present"));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
