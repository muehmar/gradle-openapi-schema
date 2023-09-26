package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class CompositionNames {
  private CompositionNames() {}

  public static Name isValidAgainstMethodName(JavaPojo pojo) {
    return Name.ofString(String.format("isValidAgainst%s", pojo.getClassName()));
  }

  public static Name asConversionMethodName(JavaPojo pojo) {
    return Name.ofString(String.format("as%s", pojo.getClassName()));
  }

  public static Name dtoMappingArgumentName(JavaPojo pojo) {
    return Name.ofString(String.format("on%s", pojo.getClassName()));
  }
}
