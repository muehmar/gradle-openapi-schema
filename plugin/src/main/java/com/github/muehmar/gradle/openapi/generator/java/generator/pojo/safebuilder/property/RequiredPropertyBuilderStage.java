package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import java.util.function.Predicate;
import lombok.Value;

@Value
public class RequiredPropertyBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  JavaPojoMember member;
  int memberIndex;
  BuilderStage nextStage;

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final PList<JavaPojoMember> requiredAdditionalProperties =
        parentPojo
            .getRequiredAdditionalProperties()
            .map(addProp -> addProp.asMember(parentPojo.getJavaPojoName()));
    final PList<IndexedMember> reversedRequiredMembers =
        parentPojo
            .getMembers()
            .filter(JavaPojoMember::isRequired)
            .filter(member -> not(existsSameAllOfMember(parentPojo, member)))
            .concat(requiredAdditionalProperties)
            .zipWithIndex()
            .map(p -> new IndexedMember(p.first(), p.second()))
            .reverse();
    final NonEmptyList<BuilderStage> nextStages = nextStages(builderVariant, parentPojo);
    return createStages(reversedRequiredMembers, builderVariant, parentPojo, nextStages.head())
        .foldRight(nextStages, NonEmptyList::cons);
  }

  private static boolean existsSameAllOfMember(JavaObjectPojo parentPojo, JavaPojoMember member) {
    final Predicate<JavaObjectPojo> hasSameAllOfMember =
        allOfSubPojo ->
            allOfSubPojo.getMembers().exists(m1 -> m1.getMemberKey().equals(member.getMemberKey()));
    return PList.fromOptional(parentPojo.getAllOfComposition())
        .flatMap(JavaAllOfComposition::getPojos)
        .exists(pojo -> hasSameAllOfMember.test(pojo) || existsSameAllOfMember(pojo, member));
  }

  private static NonEmptyList<BuilderStage> nextStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    if (builderVariant.equals(SafeBuilderVariant.FULL)) {
      return OptionalPropertyBuilderStage.createStages(builderVariant, parentPojo);
    } else {
      return LastRequiredPropertyBuilderStage.createStages(builderVariant, parentPojo);
    }
  }

  private static PList<BuilderStage> createStages(
      PList<IndexedMember> reversedMembers,
      SafeBuilderVariant builderVariant,
      JavaObjectPojo parentPojo,
      BuilderStage nextStage) {
    return reversedMembers
        .headOption()
        .map(
            member -> {
              final RequiredPropertyBuilderStage memberStage =
                  new RequiredPropertyBuilderStage(
                      builderVariant, parentPojo, member.getMember(), member.getIndex(), nextStage);
              return createStages(reversedMembers.tail(), builderVariant, parentPojo, memberStage)
                  .add(memberStage);
            })
        .orElse(PList.empty());
  }

  @Override
  public String getName() {
    return String.format("%sPropertyBuilder%d", builderVariant.getBuilderNamePrefix(), memberIndex);
  }

  @Value
  private static class IndexedMember {
    JavaPojoMember member;
    int index;
  }
}
