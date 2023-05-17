package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class NoAdditionalPropertiesValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generate_when_noAdditionalPropertiesAllowed_then_correctOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.notAllowed(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generate_when_additionalPropertiesAllowed_then_noOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.anyTypeAllowed(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_notOutput() {
    final Generator<JavaAdditionalProperties, PojoSettings> generator =
        NoAdditionalPropertiesValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaAdditionalProperties.notAllowed(),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
