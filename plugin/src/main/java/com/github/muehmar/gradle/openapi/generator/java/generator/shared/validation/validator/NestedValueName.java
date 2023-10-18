package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
class NestedValueName {
  Name name;

  public static NestedValueName fromName(Name name) {
    return new NestedValueName(name.append("Value"));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
