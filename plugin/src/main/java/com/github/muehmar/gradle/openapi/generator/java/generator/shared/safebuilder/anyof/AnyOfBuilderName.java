package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.anyof;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.name.PropertyBuilderName;
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
        .orElse(PropertyBuilderName.initial(parentPojo));
  }

  public static AnyOfBuilderName first(JavaObjectPojo parentPojo) {
    return new AnyOfBuilderName(parentPojo, BuilderType.FIRST_BUILDER);
  }

  public static AnyOfBuilderName remaining(JavaObjectPojo parentPojo) {
    return new AnyOfBuilderName(parentPojo, BuilderType.REMAINING_BUILDER);
  }

  @Override
  public String currentName() {
    return String.format("AnyOfBuilder%d", builderType.equals(BuilderType.FIRST_BUILDER) ? 0 : 1);
  }

  public BuilderName getNextBuilderName() {
    return new AnyOfBuilderName(parentPojo, BuilderType.REMAINING_BUILDER);
  }

  private enum BuilderType {
    FIRST_BUILDER,
    REMAINING_BUILDER
  }
}
