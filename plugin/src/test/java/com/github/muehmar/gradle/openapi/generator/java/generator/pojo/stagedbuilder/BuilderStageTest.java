package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.byteArrayMember;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredDouble;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.allof.AllOfBuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
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
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties")
  void
      createStages_when_allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties_matchStages(
          StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants()
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("nestedAllOfPojo"))
  void createStages_when_nestedAllOfPojo_matchStages(StagedBuilderVariant builderVariant) {
    final JavaObjectPojo nestedAllOfPojo =
        JavaPojos.allOfPojo(sampleObjectPojo1(), sampleObjectPojo2());
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(
            JavaPojos.allNecessityAndNullabilityVariants()
                .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES),
            nestedAllOfPojo);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("allOfPojoWithMembers"))
  void createStages_when_allOfPojoWithMembers_matchStages(StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(
                JavaPojos.allNecessityAndNullabilityVariants()
                    .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES),
                sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.fromMembers(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("allOfPojoWithOneOfSubPojo"))
  void createStages_when_allOfPojoWithOneOfAndAnyOfSubSchema_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo oneOfSubPojo =
        JavaPojos.oneOfPojo(sampleObjectPojo2())
            .withMembers(JavaPojoMembers.empty().add(requiredDouble()).add(requiredEmail()));
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), oneOfSubPojo)
            .withMembers(JavaPojoMembers.empty().add(requiredString()));
    ;

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("allOfPojoWithSameMemberInAllOfSubPojos"))
  void createStages_when_allOfPojoWithSameMemberInAllOfSubPojos_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo allOfPojo = JavaPojos.allOfPojo(sampleObjectPojo1(), sampleObjectPojo2());
    ;

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, allOfPojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithMembers"))
  void createStages_when_oneOfPojoWithMembers_matchStages(StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.fromMembers(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithMembers"))
  void createStages_when_anyOfPojoWithMembers_matchStages(StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo1())
            .withMembers(
                JavaPojoMembers.fromMembers(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithDiscriminatorAndMembers"))
  void createStages_when_anyOfPojoWithDiscriminatorAndMembers_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojoWithDiscriminator()
            .withMembers(
                JavaPojoMembers.fromMembers(PList.of(byteArrayMember(), optionalString())));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("allOfPojoWithRequiredProperties"))
  void createStages_when_allOfPojoWithRequiredProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithRequiredProperties"))
  void createStages_when_oneOfPojoWithRequiredProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithRequiredProperties"))
  void createStages_when_anyOfPojoWithRequiredProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo1())
            .withRequiredAdditionalProperties(PROP_2_ADDITIONAL_PROPERTIES);

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("simpleMapPojo"))
  void createStages_when_simpleMapPojo_matchStages(StagedBuilderVariant builderVariant) {
    final JavaObjectPojo simpleMapPojo =
        JavaPojos.simpleMapPojo(JavaAdditionalProperties.anyTypeAllowed());

    final NonEmptyList<BuilderStage> stages =
        BuilderStage.createStages(builderVariant, simpleMapPojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("allOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_allOfPojoWithSubschemaWithoutProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.allOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("oneOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_oneOfPojoWithSubschemaWithoutProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName(("anyOfPojoWithSubschemaWithoutProperties"))
  void createStages_when_anyOfPojoWithSubschemaWithoutProperties_matchStages(
      StagedBuilderVariant builderVariant) {
    final JavaObjectPojo pojo = JavaPojos.anyOfPojo(JavaPojos.objectPojo(PList.empty()));

    final NonEmptyList<BuilderStage> stages = BuilderStage.createStages(builderVariant, pojo);

    expect
        .scenario(builderVariant.name())
        .toMatchSnapshot(stages.map(BuilderStageTest::formatStage).toPList().mkString("\n"));
  }

  private static String formatStage(BuilderStage stage) {
    return stage.fold(
        allOfBuilderStage -> formatAllOfBuilderStage(stage, allOfBuilderStage),
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

  private static String formatAllOfBuilderStage(
      BuilderStage stage, AllOfBuilderStage allOfBuilderStage) {
    final String memberStageFormat =
        allOfBuilderStage
            .getMemberStageObjects()
            .map(
                memberStageObjects ->
                    String.format(
                        " for %s(%d), next stage: %s",
                        memberStageObjects.getMember().getName(),
                        memberStageObjects.getMemberIndex(),
                        memberStageObjects.getNextStage().getName()))
            .orElse("");
    final String pojoStageFormat =
        allOfBuilderStage
            .getSubPojoStageObjects()
            .map(
                subPojoStageObjects ->
                    String.format(
                        ", next pojo stage: %s", subPojoStageObjects.getNextStage().getName()))
            .orElse("");
    return String.format("%s%s%s", formatBase(stage), memberStageFormat, pojoStageFormat);
  }

  private static String formatBase(BuilderStage stage) {
    return String.format("%s - %s", stage.getClass().getSimpleName(), stage.getName());
  }
}
