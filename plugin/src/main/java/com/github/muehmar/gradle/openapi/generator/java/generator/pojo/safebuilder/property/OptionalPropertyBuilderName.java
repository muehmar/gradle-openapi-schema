package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class OptionalPropertyBuilderName implements BuilderName {
  private final SafeBuilderVariant builderVariant;
  private final JavaObjectPojo parentPojo;
  private final int idx;

  private OptionalPropertyBuilderName(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo, int idx) {
    this.builderVariant = builderVariant;
    this.parentPojo = parentPojo;
    this.idx = idx;
  }

  public static OptionalPropertyBuilderName initial(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return new OptionalPropertyBuilderName(builderVariant, parentPojo, 0);
  }

  public static OptionalPropertyBuilderName from(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo, int idx) {
    return new OptionalPropertyBuilderName(builderVariant, parentPojo, idx);
  }

  public OptionalPropertyBuilderName incrementIndex() {
    return new OptionalPropertyBuilderName(builderVariant, parentPojo, idx + 1);
  }

  @Override
  public String currentName() {
    return String.format("%sOptPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), idx);
  }
}
