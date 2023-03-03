package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;

public class CompositionNames {
  private CompositionNames() {}

  public static Name isValidAgainstMethodName(JavaPojo pojo) {
    return Name.ofString(String.format("isValidAgainst%s", pojo.getName().getName()));
  }
}
