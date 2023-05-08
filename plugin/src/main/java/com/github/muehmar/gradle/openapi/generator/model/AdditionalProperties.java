package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import lombok.Value;

@Value
public class AdditionalProperties {
  boolean allowed;
  Type type;

  public static AdditionalProperties anyTypeAllowed() {
    return new AdditionalProperties(true, AnyType.create());
  }

  public static AdditionalProperties allowed(Type type) {
    return new AdditionalProperties(true, type);
  }

  public static AdditionalProperties notAllowed() {
    return new AdditionalProperties(false, AnyType.create());
  }
}
