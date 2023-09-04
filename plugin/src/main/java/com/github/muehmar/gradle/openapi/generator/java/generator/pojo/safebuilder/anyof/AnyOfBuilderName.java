package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;

public class AnyOfBuilderName implements BuilderName {
  private final SafeBuilderVariant builderVariant;
  private final BuilderType builderType;

  private AnyOfBuilderName(SafeBuilderVariant builderVariant, BuilderType builderType) {
    this.builderVariant = builderVariant;
    this.builderType = builderType;
  }

  public static BuilderName initial(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return parentPojo
        .getAnyOfComposition()
        .<BuilderName>map(anyOfComposition -> first(builderVariant))
        .orElse(RequiredPropertyBuilderName.initial(builderVariant, parentPojo));
  }

  public static AnyOfBuilderName first(SafeBuilderVariant builderVariant) {
    return new AnyOfBuilderName(builderVariant, BuilderType.FIRST_BUILDER);
  }

  public static AnyOfBuilderName remaining(SafeBuilderVariant builderVariant) {
    return new AnyOfBuilderName(builderVariant, BuilderType.REMAINING_BUILDER);
  }

  public static AnyOfBuilderName last(SafeBuilderVariant builderVariant) {
    return new AnyOfBuilderName(builderVariant, BuilderType.LAST_BUILDER);
  }

  public BuilderType getBuilderType() {
    return builderType;
  }

  @Override
  public String currentName() {
    return String.format(
        "%sAnyOfBuilder%d", builderVariant.getBuilderNamePrefix(), builderType.asInt());
  }

  public BuilderName getNextBuilderName() {
    return new AnyOfBuilderName(builderVariant, BuilderType.REMAINING_BUILDER);
  }

  public enum BuilderType {
    FIRST_BUILDER(0),
    REMAINING_BUILDER(1),
    LAST_BUILDER(2);

    private final int intValue;

    BuilderType(int intValue) {
      this.intValue = intValue;
    }

    public int asInt() {
      return intValue;
    }
  }
}
