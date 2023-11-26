package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.byteArrayMember;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class BuilderStageTest {
  private Expect expect;

  private static final PList<JavaRequiredAdditionalProperty> PROP_2_ADDITIONAL_PROPERTIES =
      PList.of(
          JavaRequiredAdditionalProperty.fromNameAndType(
              Name.ofString("Prop2"), JavaTypes.stringType()));

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties")
  void
      createStages_when_allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties_matchStages(
          SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants()
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("allOfPojoWithMembers"))
  void createStages_when_allOfPojoWithMembers_matchStages(SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants()
                    .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES),
                JavaPojos.sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.leastRestrictive(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithMembers"))
  void createStages_when_oneOfPojoWithMembers_matchStages(SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants(), JavaPojos.sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.leastRestrictive(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithMembers"))
  void createStages_when_anyOfPojoWithMembers_matchStages(SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants(), JavaPojos.sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.leastRestrictive(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("allOfPojoWithRequiredProperties"))
  void createStages_when_allOfPojoWithRequiredProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants(), JavaPojos.sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithRequiredProperties"))
  void createStages_when_oneOfPojoWithRequiredProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants(), JavaPojos.sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithRequiredProperties"))
  void createStages_when_anyOfPojoWithRequiredProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants(), JavaPojos.sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("simpleMapPojo"))
  void createStages_when_simpleMapPojo_matchStages(SafeBuilderVariant builderVariant) {
    final JavaObjectPojo simpleMapPojo =
        JavaPojos.simpleMapPojo(JavaAdditionalProperties.anyTypeAllowed());

    final NonEmptyList<BuilderStage> stages =
        BuilderStage.createStages(builderVariant, simpleMapPojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("allOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_allOfPojoWithSubschemaWithoutProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.allOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_oneOfPojoWithSubschemaWithoutProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_anyOfPojoWithSubschemaWithoutProperties_matchStages(
      SafeBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.anyOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  private static String formatStage(BuilderStage stage) {
    return stage.fold(
        allOfBuilderStage ->
            String.format(
                "%s for %s(%d), next stage %s, next member stage: %s",
                formatBase(stage),
                allOfBuilderStage.getMember().map(JavaPojoMember::getName),
                allOfBuilderStage.getMemberIndex(),
                allOfBuilderStage.getNextStage().getName(),
                allOfBuilderStage.getNextMemberStage().getName()),
        oneOfBuilderStage ->
            String.format(
                "%s, next stage %s", formatBase(stage), oneOfBuilderStage.getNextStage().getName()),
        anyOfBuilderStage ->
            String.format(
                "%s, type %s and next stage %s",
                formatBase(stage),
                anyOfBuilderStage.getStageType(),
                anyOfBuilderStage.getNextStage().getName()),
        requiredPropertyBuilderStage ->
            String.format(
                "%s for %s(%d), next stage %s",
                formatBase(stage),
                requiredPropertyBuilderStage.getMember().getName(),
                requiredPropertyBuilderStage.getMemberIndex(),
                requiredPropertyBuilderStage.getNextStage().getName()),
        lastRequiredPropertyBuilderStage ->
            String.format(
                "%s, next stage %s",
                formatBase(stage), lastRequiredPropertyBuilderStage.getNextStage().getName()),
        optionalPropertyBuilderStage ->
            String.format(
                "%s for %s(%d), next stage %s",
                formatBase(stage),
                optionalPropertyBuilderStage.getMember().getName(),
                optionalPropertyBuilderStage.getIndex(),
                optionalPropertyBuilderStage.getNextStage().getName()),
        lastOptionalPropertyBuilderStage -> String.format("%s", formatBase(stage)));
  }

  private static String formatBase(BuilderStage stage) {
    return String.format("%s - %s", stage.getClass().getSimpleName(), stage.getName());
  }
}
