package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class LastRequiredPropertyBuilderStage implements BuilderStage {
  StagedBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      StagedBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final NonEmptyList<BuilderStage> nextStages =
        OptionalPropertyBuilderStage.createStages(builderVariant, parentPojo);
    final LastRequiredPropertyBuilderStage lastRequiredPropertyBuilderStage =
        new LastRequiredPropertyBuilderStage(builderVariant, parentPojo, nextStages.head());
    return nextStages.cons(lastRequiredPropertyBuilderStage);
  }

  @Override
  public String getName() {
    final int requiredMemberCount = parentPojo.getRequiredMemberCount();
    return String.format(
        "%sPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), requiredMemberCount);
  }
}
