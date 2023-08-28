package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class AnyOfBuilderName implements BuilderName {
  private final BuilderType builderType;

  private AnyOfBuilderName(BuilderType builderType) {
    this.builderType = builderType;
  }

  public static BuilderName initial(JavaObjectPojo parentPojo) {
    return parentPojo
        .getAnyOfComposition()
        .<BuilderName>map(anyOfComposition -> first())
        .orElse(RequiredPropertyBuilderName.initial(parentPojo));
  }

  public static AnyOfBuilderName first() {
    return new AnyOfBuilderName(BuilderType.FIRST_BUILDER);
  }

  public static AnyOfBuilderName remaining() {
    return new AnyOfBuilderName(BuilderType.REMAINING_BUILDER);
  }

  public static AnyOfBuilderName last() {
    return new AnyOfBuilderName(BuilderType.LAST_BUILDER);
  }

  public BuilderType getBuilderType() {
    return builderType;
  }

  @Override
  public String currentName() {
    return String.format("AnyOfBuilder%d", builderType.getIdx());
  }

  public BuilderName getNextBuilderName() {
    return new AnyOfBuilderName(BuilderType.REMAINING_BUILDER);
  }

  public enum BuilderType {
    FIRST_BUILDER(0),
    REMAINING_BUILDER(1),
    LAST_BUILDER(2);

    private final int idx;

    BuilderType(int idx) {
      this.idx = idx;
    }

    public int getIdx() {
      return idx;
    }
  }
}
