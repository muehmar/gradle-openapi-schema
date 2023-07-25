package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class OptionalPropertyBuilderName implements BuilderName {
  private final JavaObjectPojo parentPojo;
  private final int idx;

  private OptionalPropertyBuilderName(JavaObjectPojo parentPojo, int idx) {
    this.parentPojo = parentPojo;
    this.idx = idx;
  }

  public static OptionalPropertyBuilderName initial(JavaObjectPojo parentPojo) {
    return new OptionalPropertyBuilderName(parentPojo, 0);
  }

  public static OptionalPropertyBuilderName from(JavaObjectPojo parentPojo, int idx) {
    return new OptionalPropertyBuilderName(parentPojo, idx);
  }

  public OptionalPropertyBuilderName incrementIndex() {
    return new OptionalPropertyBuilderName(parentPojo, idx + 1);
  }

  @Override
  public String currentName() {
    return String.format("OptPropertyBuilder%d", idx);
  }
}
