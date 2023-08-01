package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class AnyOfBuilderName implements BuilderName {
  private final JavaObjectPojo parentPojo;
  private final BuilderType builderType;

  private AnyOfBuilderName(JavaObjectPojo parentPojo, BuilderType builderType) {
    this.parentPojo = parentPojo;
    this.builderType = builderType;
  }

  public static BuilderName initial(JavaObjectPojo parentPojo) {
    return parentPojo
        .getAnyOfComposition()
        .<BuilderName>map(anyOfComposition -> first(parentPojo))
        .orElse(RequiredPropertyBuilderName.initial(parentPojo));
  }

  public static AnyOfBuilderName first(JavaObjectPojo parentPojo) {
    return new AnyOfBuilderName(parentPojo, BuilderType.FIRST_BUILDER);
  }

  public static AnyOfBuilderName remaining(JavaObjectPojo parentPojo) {
    return new AnyOfBuilderName(parentPojo, BuilderType.REMAINING_BUILDER);
  }

  public BuilderType getBuilderType() {
    return builderType;
  }

  @Override
  public String currentName() {
    return String.format("AnyOfBuilder%d", builderType.equals(BuilderType.FIRST_BUILDER) ? 0 : 1);
  }

  public BuilderName getNextBuilderName() {
    return new AnyOfBuilderName(parentPojo, BuilderType.REMAINING_BUILDER);
  }

  public enum BuilderType {
    FIRST_BUILDER,
    REMAINING_BUILDER
  }
}
