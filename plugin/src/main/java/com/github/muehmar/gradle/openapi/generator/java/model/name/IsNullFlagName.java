package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import lombok.Value;

@Value
public class IsNullFlagName {
  JavaIdentifier name;

  private IsNullFlagName(JavaIdentifier name) {
    this.name = name;
  }

  public static IsNullFlagName fromJavaMemberName(JavaMemberName name) {
    return new IsNullFlagName(
        name.asJavaName().startUpperCase().prefix("is").append("Null").asIdentifier());
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
