package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.anyof.AnyOfBuilderGenerator.anyOfBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class AnyOfBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("anyOfPojoWithRequiredProperties")
  void generate_when_anyOfPojoWithRequiredProperties_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator = anyOfBuilderGenerator(variant);
    final JavaObjectPojo anyOfPojo =
        JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2());
    final JavaObjectPojo anyOfPojoWithMembers =
        anyOfPojo.withMembers(
            JavaPojoMembers.fromMembers(
                PList.single(TestJavaPojoMembers.requiredNullableString())));
    final Writer writer =
        generator.generate(
            anyOfPojoWithMembers, TestPojoSettings.defaultTestSettings(), javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("anyOfPojoWithoutRequiredProperties")
  void generate_when_anyOfPojoWithoutRequiredProperties_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator = anyOfBuilderGenerator(variant);
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2()),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("anyOfPojoWithOptionalProperties")
  void generate_when_anyOfPojoWithOptionalProperties_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator = anyOfBuilderGenerator(variant);

    final JavaPojoMembers members =
        JavaPojoMembers.fromMembers(PList.single(TestJavaPojoMembers.optionalBirthdate()));
    final JavaObjectPojo anyOfPojo =
        JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2())
            .withMembers(members);

    final Writer writer =
        generator.generate(anyOfPojo, TestPojoSettings.defaultTestSettings(), javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("anyOfPojoWithDiscriminatorAndMembers")
  void generate_when_anyOfPojoWithDiscriminatorAndMembers_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> generator = anyOfBuilderGenerator(variant);
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojoWithDiscriminator()
                .withMembers(JavaPojoMembers.fromMembers(PList.of(requiredBirthdate()))),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
