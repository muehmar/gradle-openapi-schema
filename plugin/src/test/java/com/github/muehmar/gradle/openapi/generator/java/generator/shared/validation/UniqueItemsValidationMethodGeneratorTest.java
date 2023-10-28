package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.UniqueItemsValidationMethodGenerator.uniqueItemsValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class UniqueItemsValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_notArrayType_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        uniqueItemsValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojoMembers.requiredDouble(),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_noUniqueItemsConstraint_then_noOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        uniqueItemsValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getArrayPojoMember(),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("validationDisabled")
  void generate_when_validationDisabled_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        uniqueItemsValidationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojoMembers.list(
                StringType.noFormat(),
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE,
                Constraints.ofUniqueItems(true)),
            defaultTestSettings().withEnableValidation(false),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("uniqueItemsConstraint")
  void generate_when_uniqueItemsConstraint_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        uniqueItemsValidationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojoMembers.list(
                StringType.noFormat(),
                Necessity.REQUIRED,
                Nullability.NOT_NULLABLE,
                Constraints.ofUniqueItems(true)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
