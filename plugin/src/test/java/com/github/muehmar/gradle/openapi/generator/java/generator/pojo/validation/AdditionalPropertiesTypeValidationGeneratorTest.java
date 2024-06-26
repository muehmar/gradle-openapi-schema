package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.AdditionalPropertiesTypeValidationGenerator.additionalPropertiesTypeValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class AdditionalPropertiesTypeValidationGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_additionalPropertiesNotAllowed_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();
    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withAdditionalProperties(JavaAdditionalProperties.notAllowed()),
            defaultTestSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_objectValueType_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();
    final Writer writer =
        generator.generate(
            sampleObjectPojo1()
                .withAdditionalProperties(JavaAdditionalProperties.allowedFor(anyType())),
            defaultTestSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("sampleObjectPojo1WithNonObjectValueType")
  void generate_when_sampleObjectPojo1WithNonObjectValueType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();

    final Writer writer =
        generator.generate(
            sampleObjectPojo1()
                .withAdditionalProperties(JavaAdditionalProperties.allowedFor(stringType())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("sampleObjectPojo1WithNullableNonObjectValueType")
  void generate_when_sampleObjectPojo1WithNullableNonObjectValueType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();

    final Writer writer =
        generator.generate(
            sampleObjectPojo1()
                .withAdditionalProperties(
                    JavaAdditionalProperties.allowedFor(stringType().withNullability(NULLABLE))),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("validationDisabled")
  void generate_when_validationDisabled_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();

    final Writer writer =
        generator.generate(
            sampleObjectPojo1()
                .withAdditionalProperties(JavaAdditionalProperties.allowedFor(stringType())),
            defaultTestSettings().withEnableValidation(false),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
