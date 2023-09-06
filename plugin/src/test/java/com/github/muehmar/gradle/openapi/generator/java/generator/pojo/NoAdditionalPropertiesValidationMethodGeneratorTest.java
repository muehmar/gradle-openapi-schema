package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class NoAdditionalPropertiesValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generate_when_noAdditionalPropertiesAllowed_then_correctOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator
            .noAdditionalPropertiesValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.notAllowed(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_additionalPropertiesAllowed_then_noOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator
            .noAdditionalPropertiesValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.anyTypeAllowed(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_notOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator
            .noAdditionalPropertiesValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.notAllowed(),
            defaultTestSettings().withEnableValidation(false),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
