package com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.NullableAdditionalPropertyClassGenerator.nullableAdditionalPropertyClassGenerator;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class NullableAdditionalPropertyClassGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("nullableAdditionalPropertyClassGenerator")
  void nullableAdditionalPropertyClassGenerator_when_used_then_matchSnapshot() {
    final Generator<Void, Void> generator = nullableAdditionalPropertyClassGenerator();

    final Writer writer = generator.generate(noData(), noSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
