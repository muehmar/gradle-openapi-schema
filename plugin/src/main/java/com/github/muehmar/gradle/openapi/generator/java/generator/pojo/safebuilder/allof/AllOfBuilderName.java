package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AllOfBuilderName implements BuilderName {
  private final SafeBuilderVariant builderVariant;
  private final JavaObjectPojo parentPojo;
  private final JavaAllOfComposition allOfComposition;
  private final JavaObjectPojo allOfPojo;
  private final int memberIndex;

  private AllOfBuilderName(
      SafeBuilderVariant builderVariant,
      JavaObjectPojo parentPojo,
      JavaAllOfComposition allOfComposition,
      JavaObjectPojo allOfPojo,
      int memberIndex) {
    this.builderVariant = builderVariant;
    this.parentPojo = parentPojo;
    this.allOfComposition = allOfComposition;
    this.allOfPojo = allOfPojo;
    this.memberIndex = memberIndex;
  }

  public static BuilderName initial(SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
    return parentPojo
        .getAllOfComposition()
        .<BuilderName>flatMap(
            allOfComposition -> {
              final JavaObjectPojo allOfPojo = allOfComposition.getPojos().head();
              return allOfPojo
                  .getAllMembers()
                  .headOption()
                  .map(
                      member ->
                          new AllOfBuilderName(
                              builderVariant, parentPojo, allOfComposition, allOfPojo, 0));
            })
        .orElse(OneOfBuilderName.initial(builderVariant, parentPojo));
  }

  public static AllOfBuilderName of(
      SafeBuilderVariant builderVariant,
      JavaObjectPojo parentPojo,
      JavaAllOfComposition allOfComposition,
      JavaObjectPojo allOfPojo,
      int idx) {
    return new AllOfBuilderName(builderVariant, parentPojo, allOfComposition, allOfPojo, idx);
  }

  @Override
  public String currentName() {
    return String.format(
        "%sAllOfBuilder%s%d",
        builderVariant.getBuilderNamePrefix(), allOfPojo.getSchemaName(), memberIndex);
  }

  private BuilderName incrementMemberIndex() {
    return new AllOfBuilderName(
        builderVariant, parentPojo, allOfComposition, allOfPojo, memberIndex + 1);
  }

  public BuilderName getNextBuilderName() {
    return memberIndex + 1 >= allOfPojo.getAllMembers().size()
        ? getNextPojoBuilderName()
        : incrementMemberIndex();
  }

  public BuilderName getNextPojoBuilderName() {
    final int currentAllOfPojoIndex =
        allOfComposition
            .getPojos()
            .zipWithIndex()
            .toPList()
            .find(p -> p.first().equals(allOfPojo))
            .map(Pair::second)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Invalid AllOfBuilderName: AllOfPojo "
                            + allOfPojo.getClassName()
                            + " is not part of the parent pojo "
                            + parentPojo.getClassName()));
    final Optional<BuilderName> nextAllOfBuilderName =
        allOfComposition
            .getPojos()
            .toPList()
            .drop(currentAllOfPojoIndex + 1)
            .headOption()
            .map(
                nextAllOfPojo ->
                    new AllOfBuilderName(
                        builderVariant, parentPojo, allOfComposition, nextAllOfPojo, 0));
    return nextAllOfBuilderName.orElse(OneOfBuilderName.initial(builderVariant, parentPojo));
  }
}
