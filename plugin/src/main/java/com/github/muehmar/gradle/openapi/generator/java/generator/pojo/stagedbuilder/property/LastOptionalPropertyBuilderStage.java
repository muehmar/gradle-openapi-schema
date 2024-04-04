package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class LastOptionalPropertyBuilderStage implements BuilderStage {
  StagedBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;

  public static NonEmptyList<BuilderStage> createStages(
      StagedBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return NonEmptyList.single(new LastOptionalPropertyBuilderStage(builderVariant, parentPojo));
  }

  @Override
  public String getName() {
    final int optionalMemberCount =
        parentPojo.getMembers().filter(JavaPojoMember::isOptional).size();
    return String.format(
        "%sOptPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), optionalMemberCount);
  }
}
