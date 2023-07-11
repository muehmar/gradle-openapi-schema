package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.name;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class PropertyBuilderName implements BuilderName {
  private final JavaObjectPojo parentPojo;
  private final int idx;

  private PropertyBuilderName(JavaObjectPojo parentPojo, int idx) {
    this.parentPojo = parentPojo;
    this.idx = idx;
  }

  public static PropertyBuilderName initial(JavaObjectPojo parentPojo) {
    return new PropertyBuilderName(parentPojo, 0);
  }

  @Override
  public String currentName() {
    return String.format("PropertyBuilder%d", idx);
  }
}
