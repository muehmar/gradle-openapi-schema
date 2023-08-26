package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AdditionalPropertiesTypeValidationGenerator.additionalPropertiesTypeValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.withAdditionalProperties;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class AdditionalPropertiesTypeValidationGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_additionalPropertiesNotAllowed_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();
    final Writer writer =
        generator.generate(
            withAdditionalProperties(sampleObjectPojo1(), JavaAdditionalProperties.notAllowed()),
            TestPojoSettings.defaultSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_objectValueType_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();
    final Writer writer =
        generator.generate(
            withAdditionalProperties(
                sampleObjectPojo1(), JavaAdditionalProperties.allowedFor(javaAnyType())),
            TestPojoSettings.defaultSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("sampleObjectPojo1WithNonObjectValueType")
  void generate_when_sampleObjectPojo1WithNonObjectValueType_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();

    final Writer writer =
        generator.generate(
            withAdditionalProperties(
                sampleObjectPojo1(), JavaAdditionalProperties.allowedFor(stringType())),
            TestPojoSettings.defaultSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_validationDisabled_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        additionalPropertiesTypeValidationGenerator();

    final Writer writer =
        generator.generate(
            withAdditionalProperties(
                sampleObjectPojo1(), JavaAdditionalProperties.allowedFor(stringType())),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
