package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class IsNullFlagName {
  JavaIdentifier name;

  private IsNullFlagName(JavaIdentifier name) {
    this.name = name;
  }

  public static IsNullFlagName fromName(Name name) {
    return new IsNullFlagName(
        JavaIdentifier.fromName(name.startUpperCase().prefix("is").append("Null")));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
