package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SnapshotExtension.class)
class DiscriminatorValidationMethodGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(ComposedPojo.CompositionType.class)
  @SnapshotName("NoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_noContent(ComposedPojo.CompositionType type) {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(type),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.scenario(type.name()).toMatchSnapshot(writer.asString());
  }

  @ParameterizedTest
  @EnumSource(ComposedPojo.CompositionType.class)
  @SnapshotName("DiscriminatorWithoutMapping")
  void generate_when_calledWithDiscriminator_then_noContent(ComposedPojo.CompositionType type) {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojoWithDiscriminator(type),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.scenario(type.name()).toMatchSnapshot(writer.asString());
  }

  @ParameterizedTest
  @EnumSource(ComposedPojo.CompositionType.class)
  @SnapshotName("DiscriminatorWithMapping")
  void generate_when_calledWithDiscriminatorAndMapping_then_noContent(
      ComposedPojo.CompositionType type) {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojoWithDiscriminatorMapping(type),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.scenario(type.name()).toMatchSnapshot(writer.asString());
  }
}
