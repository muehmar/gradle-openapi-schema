package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import lombok.Value;

@Value
public class IsPresentFlagName {
  JavaIdentifier name;

  private IsPresentFlagName(JavaIdentifier name) {
    this.name = name;
  }

  public static IsPresentFlagName fromJavaMemberName(JavaMemberName name) {
    return new IsPresentFlagName(
        name.asJavaName().startUpperCase().prefix("is").append("Present").asIdentifier());
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
