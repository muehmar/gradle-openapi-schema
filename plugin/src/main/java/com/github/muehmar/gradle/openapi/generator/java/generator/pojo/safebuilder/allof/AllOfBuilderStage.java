package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderStageBuilder.fullAllOfBuilderStageBuilder;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

@PojoBuilder
@Value
public class AllOfBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  JavaAllOfComposition allOfComposition;
  JavaObjectPojo allOfSubPojo;
  Optional<JavaPojoMember> member;
  int memberIndex;
  BuilderStage nextStage;
  BuilderStage nextMemberStage;

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final NonEmptyList<BuilderStage> nextStages =
        OneOfBuilderStage.createStages(builderVariant, parentPojo);
    final PList<BuilderStage> allOfStages =
        parentPojo
            .getAllOfComposition()
            .map(
                allOfComposition ->
                    new AllOfStageObjects(builderVariant, parentPojo, allOfComposition))
            .map(allOfStageObjects -> createStages(allOfStageObjects, nextStages.head()))
            .orElseGet(PList::empty);
    return allOfStages.foldRight(nextStages, NonEmptyList::cons);
  }

  private static PList<BuilderStage> createStages(
      AllOfStageObjects allOfStageObjects, BuilderStage nextStage) {
    final PList<JavaObjectPojo> reversedAllOfSubPojos =
        allOfStageObjects.allOfComposition.getPojos().toPList().reverse();
    return createStages(allOfStageObjects, reversedAllOfSubPojos, nextStage);
  }

  private static PList<BuilderStage> createStages(
      AllOfStageObjects allOfStageObjects,
      PList<JavaObjectPojo> reversedAllOfSubPojos,
      BuilderStage nextStage) {
    return reversedAllOfSubPojos
        .headOption()
        .map(
            allOfSubPojo -> {
              final PojoStageObjects pojoStageObjects = allOfStageObjects.addSubPojo(allOfSubPojo);
              final PList<BuilderStage> pojoStages = createPojoStages(pojoStageObjects, nextStage);
              return createStages(
                      allOfStageObjects,
                      reversedAllOfSubPojos.tail(),
                      pojoStages.headOption().orElse(nextStage))
                  .concat(pojoStages);
            })
        .orElse(PList.empty());
  }

  private static PList<BuilderStage> createPojoStages(
      PojoStageObjects objects, BuilderStage nextStage) {
    final PList<JavaPojoMember> requiredPropertiesAsMembers =
        objects
            .getAllOfSubPojo()
            .getRequiredAdditionalProperties()
            .map(reqProp -> reqProp.asMember(objects.getAllOfSubPojo().getJavaPojoName()));
    final PList<IndexedPojoMember> reversedMembersForStages =
        objects
            .getAllOfSubPojo()
            .getMembers()
            .map(member -> member.asInnerEnumOf(objects.getAllOfSubPojo().getClassName()))
            .concat(requiredPropertiesAsMembers)
            .zipWithIndex()
            .map(p -> new IndexedPojoMember(p.first(), p.second()))
            .reverse();
    if (reversedMembersForStages.isEmpty() || objects.getAllOfSubPojo().hasCompositions()) {
      return PList.single(createPojoMemberStage(objects, Optional.empty(), nextStage, nextStage));
    } else {
      return createPojoMemberStages(objects, reversedMembersForStages, nextStage, nextStage);
    }
  }

  private static PList<BuilderStage> createPojoMemberStages(
      PojoStageObjects objects,
      PList<IndexedPojoMember> reversedMembers,
      BuilderStage nextStage,
      BuilderStage nextMemberStage) {
    return reversedMembers
        .headOption()
        .map(
            member -> {
              final BuilderStage pojoMemberStage =
                  createPojoMemberStage(objects, Optional.of(member), nextStage, nextMemberStage);
              return createPojoMemberStages(
                      objects, reversedMembers.tail(), nextStage, pojoMemberStage)
                  .add(pojoMemberStage);
            })
        .orElse(PList.empty());
  }

  private static BuilderStage createPojoMemberStage(
      PojoStageObjects objects,
      Optional<IndexedPojoMember> pojoMember,
      BuilderStage nextStage,
      BuilderStage nextMemberStage) {
    return fullAllOfBuilderStageBuilder()
        .builderVariant(objects.builderVariant)
        .parentPojo(objects.parentPojo)
        .allOfComposition(objects.allOfComposition)
        .allOfSubPojo(objects.allOfSubPojo)
        .memberIndex(pojoMember.map(IndexedPojoMember::getIndex).orElse(0))
        .nextStage(nextStage)
        .nextMemberStage(nextMemberStage)
        .member(pojoMember.map(IndexedPojoMember::getMember))
        .build();
  }

  @Override
  public String getName() {
    return String.format(
        "%sAllOfBuilder%s%d",
        builderVariant.getBuilderNamePrefix(), allOfSubPojo.getSchemaName(), memberIndex);
  }

  @PojoBuilder
  @Value
  static class AllOfStageObjects {
    SafeBuilderVariant builderVariant;
    JavaObjectPojo parentPojo;
    JavaAllOfComposition allOfComposition;

    PojoStageObjects addSubPojo(JavaObjectPojo allOfSubPojo) {
      return new PojoStageObjects(builderVariant, parentPojo, allOfComposition, allOfSubPojo);
    }
  }

  @PojoBuilder
  @Value
  static class PojoStageObjects {
    SafeBuilderVariant builderVariant;
    JavaObjectPojo parentPojo;
    JavaAllOfComposition allOfComposition;
    JavaObjectPojo allOfSubPojo;
  }

  @Value
  private static class IndexedPojoMember {
    JavaPojoMember member;
    int index;
  }
}
