package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class ComposedPojoGeneratorTest {

  private Expect expect;

  @Test
  @SnapshotName("composedPojoAndDefaultSettings")
  void generate_when_composedPojoWithDefaultSettings_then_matchSnapshot() {
    final ComposedPojoGenerator generator = new ComposedPojoGenerator();

    final Writer writer =
        generator.generate(
            (JavaComposedPojo) JavaPojos.composedPojo(ComposedPojo.CompositionType.ONE_OF),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
