package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class AnyOfBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  StageType stageType;
  JavaObjectPojo parentPojo;
  JavaAnyOfComposition anyOfComposition;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final NonEmptyList<BuilderStage> nextStages =
        RequiredPropertyBuilderStage.createStages(builderVariant, parentPojo);
    return parentPojo
        .getAnyOfComposition()
        .map(
            anyOfComposition -> {
              final PList<AnyOfBuilderStage> anyOfStages =
                  PList.of(StageType.FIRST_STAGE, StageType.REMAINING_STAGE, StageType.LAST_STAGE)
                      .map(
                          stageType ->
                              new AnyOfBuilderStage(
                                  builderVariant,
                                  stageType,
                                  parentPojo,
                                  anyOfComposition,
                                  nextStages.head()));
              return anyOfStages.foldRight(nextStages, NonEmptyList::cons);
            })
        .orElse(nextStages);
  }

  public BuilderStage getNextPropertyBuilderStage() {
    return RequiredPropertyBuilderStage.createStages(builderVariant, parentPojo).head();
  }

  public BuilderStage getNextAnyOfStage() {
    return new AnyOfBuilderStage(
        builderVariant, StageType.REMAINING_STAGE, parentPojo, anyOfComposition, nextStage);
  }

  public BuilderStage getLastStage() {
    return new AnyOfBuilderStage(
        builderVariant, StageType.LAST_STAGE, parentPojo, anyOfComposition, nextStage);
  }

  @Override
  public String getName() {
    return String.format(
        "%sAnyOfBuilder%d", builderVariant.getBuilderNamePrefix(), stageType.asInt());
  }

  public enum StageType {
    FIRST_STAGE(0),
    REMAINING_STAGE(1),
    LAST_STAGE(2);

    private final int intValue;

    StageType(int intValue) {
      this.intValue = intValue;
    }

    public int asInt() {
      return intValue;
    }
  }
}
