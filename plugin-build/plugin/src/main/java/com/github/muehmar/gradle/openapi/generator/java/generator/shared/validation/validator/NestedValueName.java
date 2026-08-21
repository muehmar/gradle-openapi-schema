package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import lombok.Value;

@Value
class NestedValueName {
  JavaName name;

  public static NestedValueName fromName(JavaName name) {
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
