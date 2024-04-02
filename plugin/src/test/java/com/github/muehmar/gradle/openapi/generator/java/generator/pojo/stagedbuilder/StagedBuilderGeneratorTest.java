package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class StagedBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("enabledSafeBuilder")
  void generate_when_enabledSafeBuilder_then_correctOutput() {
    final StagedBuilderGenerator gen = new StagedBuilderGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            defaultTestSettings().withEnableSafeBuilder(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("disabledSafeBuilder")
  void generate_when_disabledSafeBuilder_then_correctOutput() {
    final StagedBuilderGenerator gen = new StagedBuilderGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            defaultTestSettings().withEnableSafeBuilder(false),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
