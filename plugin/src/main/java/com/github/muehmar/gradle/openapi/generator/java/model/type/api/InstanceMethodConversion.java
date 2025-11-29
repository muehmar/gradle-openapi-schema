package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class InstanceMethodConversion {
  Name methodName;

  public static InstanceMethodConversion ofString(String methodName) {
    return new InstanceMethodConversion(Name.ofString(methodName));
  }

  public InstanceMethodConversion replaceClassName(
      QualifiedClassName currentClassName, QualifiedClassName newClassName) {
    // Instance methods don't reference a class name, so just return this unchanged
    return this;
  }
}
