package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.UnmapMapMethod.unmapMapMethod;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class UnmapMapMethodTest {
  private Expect expect;

  @Test
  void unmapMapMethod_when_called_then_correctOutput() {
    final Generator<Void, Void> generator = unmapMapMethod();
    final Writer writer = generator.generate(noData(), noSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
