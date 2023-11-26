package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class LastOptionalPropertyBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
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
