package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderStageBuilder.fullAllOfBuilderStageBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.HashSet;
import java.util.Optional;
import lombok.Value;

@PojoBuilder
@Value
public class AllOfBuilderStage implements BuilderStage {
  SafeBuilderVariant builderVariant;
  JavaObjectPojo parentPojo;
  JavaAllOfComposition allOfComposition;
  JavaObjectPojo allOfSubPojo;

  Optional<MemberStageObjects> memberStageObjects;
  Optional<SubPojoStageObjects> subPojoStageObjects;

  @Value
  public static class MemberStageObjects {
    JavaPojoMember member;
    int memberIndex;
    BuilderStage nextStage;
  }

  @Value
  public static class SubPojoStageObjects {
    BuilderStage nextStage;
  }

  private AllOfBuilderStage merge(AllOfBuilderStage other) {
    return fullAllOfBuilderStageBuilder()
        .builderVariant(builderVariant)
        .parentPojo(parentPojo)
        .allOfComposition(allOfComposition)
        .allOfSubPojo(allOfSubPojo)
        .memberStageObjects(Optionals.or(memberStageObjects, other.getMemberStageObjects()))
        .subPojoStageObjects(Optionals.or(subPojoStageObjects, other.getSubPojoStageObjects()))
        .build();
  }

  public static NonEmptyList<BuilderStage> createStages(
      SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    final NonEmptyList<BuilderStage> nextStages =
        OneOfBuilderStage.createStages(builderVariant, parentPojo);

    final Optional<AllOfStageObjects> allOfStageObjects =
        parentPojo
            .getAllOfComposition()
            .map(
                allOfComposition ->
                    new AllOfStageObjects(builderVariant, parentPojo, allOfComposition));

    final PList<AllOfBuilderStage> allOfMemberStages =
        allOfStageObjects
            .map(objects -> objects.getMemberAllOfStages(nextStages.head()))
            .orElseGet(PList::empty);

    final PList<AllOfBuilderStage> allOfPojoStages =
        allOfStageObjects
            .map(objects -> objects.getPojoAllOfStages(nextStages.head()))
            .orElseGet(PList::empty);

    final PList<AllOfBuilderStage> firstMergedStage =
        allOfMemberStages
            .headOption()
            .flatMap(firstMemberStage -> allOfPojoStages.headOption().map(firstMemberStage::merge))
            .map(PList::single)
            .orElseGet(PList::empty);

    return firstMergedStage
        .concat(allOfMemberStages.drop(firstMergedStage.size()))
        .concat(allOfPojoStages.drop(firstMergedStage.size()))
        .foldRight(nextStages, NonEmptyList::cons);
  }

  @Override
  public String getName() {
    final String suffix =
        memberStageObjects
            .map(MemberStageObjects::getMemberIndex)
            .map(index -> String.format("%d", index))
            .orElse("");
    return String.format(
        "%sAllOfBuilder%s%s",
        builderVariant.getBuilderNamePrefix(), allOfSubPojo.getSchemaName(), suffix);
  }

  @PojoBuilder
  @Value
  static class AllOfStageObjects {
    SafeBuilderVariant builderVariant;
    JavaObjectPojo parentPojo;
    JavaAllOfComposition allOfComposition;

    PList<AllOfBuilderStage> getMemberAllOfStages(BuilderStage nextStage) {
      if (hasNoAnyOfOrOneOfComposition()) {
        final HashSet<JavaName> seenNames = new HashSet<>();
        return allOfComposition
            .getPojos()
            .toPList()
            .reverse()
            .flatMap(
                allOfSubPojo1 ->
                    new AllOfSubPojoWrapper(allOfSubPojo1)
                        .getMembersForBuilder()
                        .zipWithIndex()
                        .reverse()
                        .map(
                            p -> {
                              final JavaPojoMember member1 = p.first();
                              final int index = p.second();
                              return new MemberObjects(
                                  builderVariant,
                                  parentPojo,
                                  allOfComposition,
                                  allOfSubPojo1,
                                  member1,
                                  index);
                            }))
            .reverse()
            .filter(m -> seenNames.add(m.getMember().getName()))
            .reverse()
            .foldLeft(
                PList.empty(),
                (stages, member) -> {
                  final MemberStageObjects memberStageObjects =
                      member.toMemberStageObjects(
                          stages.headOption().<BuilderStage>map(s -> s).orElse(nextStage));
                  final AllOfBuilderStage allOfBuilderStage =
                      fullAllOfBuilderStageBuilder()
                          .builderVariant(builderVariant)
                          .parentPojo(parentPojo)
                          .allOfComposition(allOfComposition)
                          .allOfSubPojo(member.getAllOfSubPojo())
                          .memberStageObjects(memberStageObjects)
                          .subPojoStageObjects(Optional.empty())
                          .build();
                  return stages.cons(allOfBuilderStage);
                });
      } else {
        return PList.empty();
      }
    }

    PList<AllOfBuilderStage> getPojoAllOfStages(BuilderStage nextStage) {
      return allOfComposition
          .getPojos()
          .reverse()
          .toPList()
          .foldLeft(
              PList.empty(),
              (stages, allOfSubPojo) -> {
                final SubPojoStageObjects subPojoStageObjects =
                    new SubPojoStageObjects(
                        stages.headOption().<BuilderStage>map(s -> s).orElse(nextStage));
                final AllOfBuilderStage allOfBuilderStage =
                    fullAllOfBuilderStageBuilder()
                        .builderVariant(builderVariant)
                        .parentPojo(parentPojo)
                        .allOfComposition(allOfComposition)
                        .allOfSubPojo(allOfSubPojo)
                        .memberStageObjects(Optional.empty())
                        .subPojoStageObjects(subPojoStageObjects)
                        .build();
                return stages.cons(allOfBuilderStage);
              });
    }

    boolean hasNoAnyOfOrOneOfComposition() {
      return allOfComposition
          .getPojos()
          .toPList()
          .map(AllOfSubPojoWrapper::new)
          .forall(AllOfSubPojoWrapper::hasNoAnyOfOrOneOfComposition);
    }
  }

  @Value
  static class AllOfSubPojoWrapper {
    JavaObjectPojo allOfSubPojo;

    PList<JavaPojoMember> getMembersForBuilder() {
      final PList<JavaPojoMember> allOfMembers =
          allOfSubPojo
              .getAllOfComposition()
              .map(c -> c.getPojos().toPList().map(AllOfSubPojoWrapper::new))
              .orElse(PList.empty())
              .flatMap(AllOfSubPojoWrapper::getMembersForBuilder);
      final PList<JavaPojoMember> requiredPropertiesAsMembers =
          allOfSubPojo
              .getRequiredAdditionalProperties()
              .map(reqProp -> reqProp.asMember(allOfSubPojo.getJavaPojoName()));
      final PList<JavaPojoMember> members =
          allOfSubPojo
              .getMembers()
              .map(member -> member.asInnerEnumOf(allOfSubPojo.getClassName()));
      return members.concat(requiredPropertiesAsMembers).concat(allOfMembers);
    }

    boolean hasNoAnyOfOrOneOfComposition() {
      return not(
              allOfSubPojo.getOneOfComposition().isPresent()
                  || allOfSubPojo.getAnyOfComposition().isPresent())
          && allOfSubPojo
              .getAnyOfComposition()
              .map(
                  c ->
                      c.getPojos()
                          .map(AllOfSubPojoWrapper::new)
                          .toPList()
                          .forall(AllOfSubPojoWrapper::hasNoAnyOfOrOneOfComposition))
              .orElse(true);
    }
  }

  @Value
  private static class MemberObjects {
    SafeBuilderVariant builderVariant;
    JavaObjectPojo parentPojo;
    JavaAllOfComposition allOfComposition;
    JavaObjectPojo allOfSubPojo;
    JavaPojoMember member;
    int index;

    MemberStageObjects toMemberStageObjects(BuilderStage nextStage) {
      return new MemberStageObjects(member, index, nextStage);
    }
  }
}
