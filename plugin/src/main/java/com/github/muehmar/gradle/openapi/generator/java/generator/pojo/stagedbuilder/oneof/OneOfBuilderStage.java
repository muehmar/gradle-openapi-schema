package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.oneof;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.anyof.AnyOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class OneOfBuilderStage implements BuilderStage {
  StagedBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  JavaOneOfComposition oneOfComposition;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      StagedBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final NonEmptyList<BuilderStage> nextStages =
        AnyOfBuilderStage.createStages(builderVariant, parentPojo);
    return parentPojo
        .getOneOfComposition()
        .map(
            oneOfComposition ->
                new OneOfBuilderStage(
                    builderVariant, parentPojo, oneOfComposition, nextStages.head()))
        .map(nextStages::cons)
        .orElse(nextStages);
  }

  @Override
  public String getName() {
    return String.format("%sOneOfBuilder", builderVariant.getBuilderNamePrefix());
  }
}
