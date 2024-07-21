package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.OptionalMemberBuilderGenerator.optionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class OptionalMemberBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen = optionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(), defaultTestSettings(), javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariantsFullyTypeMapped")
  void generate_when_allNecessityAndNullabilityVariantsFullyTypeMapped_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen = optionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariantsFullyTypeMapped(),
            defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
