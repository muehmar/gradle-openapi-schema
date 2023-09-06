package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.InvalidCompositionDtoGetterGenerator.invalidCompositionDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class InvalidCompositionDtoGetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        invalidCompositionDtoGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void generate_when_anyOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        invalidCompositionDtoGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_objectPojoWithoutCompositions_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        invalidCompositionDtoGetterGenerator();

    final Writer writer =
        generator.generate(sampleObjectPojo1(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        invalidCompositionDtoGetterGenerator();

    final Writer writer =
        generator.generate(
            sampleObjectPojo1(), defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals("", writer.asString());
  }
}
