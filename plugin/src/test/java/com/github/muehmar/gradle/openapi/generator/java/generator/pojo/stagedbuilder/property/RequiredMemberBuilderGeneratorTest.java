package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.RequiredMemberBuilderGenerator.requiredMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class RequiredMemberBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen = requiredMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariantsFullyTypeMapped")
  void generate_when_allNecessityAndNullabilityVariantsFullyTypeMapped_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen = requiredMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariantsFullyTypeMapped(),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties")
  void
      generate_when_allNecessityAndNullabilityVariantsWithRequiredAdditionalProperties_then_correctOutput(
          StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen = requiredMemberBuilderGenerator(variant);
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(
                Name.ofString("prop1"), javaAnyType(NULLABLE)));

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants()
                .withRequiredAdditionalProperties(requiredAdditionalProperties),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("builderMethodsOfFirstRequiredMemberGeneratorWithRequiredProperty")
  void builderMethodsOfFirstRequiredMemberGenerator_when_hasRequiredProperty_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<RequiredPropertyBuilderStage, PojoSettings> gen =
        RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator();

    final JavaPojoMember javaPojoMember = TestJavaPojoMembers.requiredNullableBirthdate();
    final BuilderStage stage =
        RequiredPropertyBuilderStage.createStages(variant, JavaPojos.objectPojo(javaPojoMember))
            .head();
    final Optional<RequiredPropertyBuilderStage> requiredPropertyBuilderStage =
        stage.asRequiredPropertyBuilderStage();

    assertTrue(requiredPropertyBuilderStage.isPresent());

    final Writer writer =
        gen.generate(
            requiredPropertyBuilderStage.get(),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
