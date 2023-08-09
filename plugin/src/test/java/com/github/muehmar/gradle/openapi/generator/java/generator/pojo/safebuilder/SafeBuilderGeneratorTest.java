package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class SafeBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("enabledSafeBuilder")
  void generate_when_enabledSafeBuilder_then_correctOutput() {
    final SafeBuilderGenerator gen = new SafeBuilderGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withEnableSafeBuilder(true),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("disabledSafeBuilder")
  void generate_when_disabledSafeBuilder_then_correctOutput() {
    final SafeBuilderGenerator gen = new SafeBuilderGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withEnableSafeBuilder(false),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}