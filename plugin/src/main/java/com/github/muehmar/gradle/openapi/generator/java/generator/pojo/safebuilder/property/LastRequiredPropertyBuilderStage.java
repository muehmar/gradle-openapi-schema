package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class LastRequiredPropertyBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
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
