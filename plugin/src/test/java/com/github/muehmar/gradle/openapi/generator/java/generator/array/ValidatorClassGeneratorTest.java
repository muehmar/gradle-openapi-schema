package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.java.generator.array.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ValidatorClassGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator = validationClassGenerator();

    final Writer writer =
        generator.generate(JavaPojos.arrayPojo(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojoWithUniqueItems")
  void generate_when_arrayPojoWithUniqueItems_then_correctOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator = validationClassGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
