package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.ConstructorContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class PojoConstructorGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("objectPojo")
  void generator_when_objectPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator = pojoConstructorGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getConstructorContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("necessityAndNullabilityObjectPojo")
  void generator_when_necessityAndNullabilityObjectPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator = pojoConstructorGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getConstructorContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generator_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator = pojoConstructorGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.illegalIdentifierPojo().getConstructorContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojo")
  void generator_when_arrayPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator = pojoConstructorGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getConstructorContent(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
