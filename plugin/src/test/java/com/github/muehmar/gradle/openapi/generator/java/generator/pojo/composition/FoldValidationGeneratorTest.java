package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldValidationGenerator.foldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class FoldValidationGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOf")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldValidationGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfProtectedAndDeprecatedSettings")
  void generate_when_protectedAndDeprecatedSettings_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldValidationGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods()
                        .withModifier(JavaModifier.PROTECTED)
                        .withDeprecatedAnnotation(true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldValidationGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOfProtectedAndDeprecatedSettings")
  void generate_when_anyOfProtectedAndDeprecatedSettings_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldValidationGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods()
                        .withModifier(JavaModifier.PROTECTED)
                        .withDeprecatedAnnotation(true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
