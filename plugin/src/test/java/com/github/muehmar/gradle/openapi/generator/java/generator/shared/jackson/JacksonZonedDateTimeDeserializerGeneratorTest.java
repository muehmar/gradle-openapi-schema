package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JacksonZonedDateTimeDeserializerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("zonedDateTimeDeserializerJackson2")
  void zonedDateTimeDeserializer_when_jackson2_then_matchSnapshot() {
    final Generator<Void, PojoSettings> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer =
        generator.generate(
            noData(),
            defaultTestSettings().withJsonSupport(JsonSupport.JACKSON_2),
            Writer.javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("zonedDateTimeDeserializerJackson3")
  void zonedDateTimeDeserializer_when_jackson3_then_matchSnapshot() {
    final Generator<Void, PojoSettings> generator =
        JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();

    final Writer writer = generator.generate(noData(), defaultTestSettings(), Writer.javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
