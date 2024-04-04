package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

@Value
public class OptionalPropertyBuilderStage implements BuilderStage {
  StagedBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  JavaPojoMember member;
  int index;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      StagedBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final PList<IndexedMember> reversedOptionalMembers =
        parentPojo
            .getMembers()
            .filter(JavaPojoMember::isOptional)
            .zipWithIndex()
            .map(p -> new IndexedMember(p.first(), p.second()))
            .reverse();
    final NonEmptyList<BuilderStage> nextStages =
        LastOptionalPropertyBuilderStage.createStages(builderVariant, parentPojo);
    return createStages(reversedOptionalMembers, builderVariant, parentPojo, nextStages.head())
        .foldRight(nextStages, NonEmptyList::cons);
  }

  private static PList<BuilderStage> createStages(
      PList<IndexedMember> reversedMembers,
      StagedBuilderVariant builderVariant,
      JavaObjectPojo parentPojo,
      BuilderStage nextStage) {
    return reversedMembers
        .headOption()
        .map(
            member -> {
              final OptionalPropertyBuilderStage memberStage =
                  new OptionalPropertyBuilderStage(
                      builderVariant, parentPojo, member.getMember(), member.getIndex(), nextStage);
              return createStages(reversedMembers.tail(), builderVariant, parentPojo, memberStage)
                  .add(memberStage);
            })
        .orElse(PList.empty());
  }

  @Override
  public String getName() {
    return String.format("%sOptPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), index);
  }

  @Value
  private static class IndexedMember {
    JavaPojoMember member;
    int index;
  }
}
