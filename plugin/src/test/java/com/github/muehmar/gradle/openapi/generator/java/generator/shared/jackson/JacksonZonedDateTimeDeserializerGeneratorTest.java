package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JacksonZonedDateTimeDeserializerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("zonedDateTimeDeserializer")
  void zonedDateTimeDeserializer_when_generate_then_matchSnapshot() {
    final Generator<Void, Void> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer = generator.generate(noData(), noSettings(), Writer.javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
