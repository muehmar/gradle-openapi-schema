package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class RequiredPropertyBuilderName implements BuilderName {
  private final JavaObjectPojo parentPojo;
  private final int idx;

  private RequiredPropertyBuilderName(JavaObjectPojo parentPojo, int idx) {
    this.parentPojo = parentPojo;
    this.idx = idx;
  }

  public static RequiredPropertyBuilderName initial(JavaObjectPojo parentPojo) {
    return new RequiredPropertyBuilderName(parentPojo, 0);
  }

  public static RequiredPropertyBuilderName from(JavaObjectPojo parentPojo, int idx) {
    return new RequiredPropertyBuilderName(parentPojo, idx);
  }

  public RequiredPropertyBuilderName incrementIndex() {
    return new RequiredPropertyBuilderName(parentPojo, idx + 1);
  }

  @Override
  public String currentName() {
    return String.format("PropertyBuilder%d", idx);
  }
}
