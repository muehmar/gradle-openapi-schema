package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class TristateGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("tristateClass")
  void tristateClass_when_called_then_correctOutput() {
    final Generator<Void, Void> gen = TristateGenerator.tristateClass();
    final Writer writer = gen.generate(noData(), noSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
