package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class IsPresentFlagName {
  JavaIdentifier name;

  private IsPresentFlagName(JavaIdentifier name) {
    this.name = name;
  }

  public static IsPresentFlagName fromName(Name name) {
    return new IsPresentFlagName(
        JavaIdentifier.fromName(name.startUpperCase().prefix("is").append("Present")));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
