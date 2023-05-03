package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class UniqueItemsValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_noUniqueItemsConstraint_then_noOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator =
        UniqueItemsValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_noOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator =
        UniqueItemsValidationMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("uniqueItemsConstraint")
  void generate_when_uniqueItemsConstraint_then_correctOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator =
        UniqueItemsValidationMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_HASH_SET::equals));
    expect.toMatchSnapshot(writer.asString());
  }
}
