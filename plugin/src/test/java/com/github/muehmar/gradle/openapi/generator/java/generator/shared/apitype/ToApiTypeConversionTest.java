package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ToApiTypeConversionTest {
  private Expect expect;

  @Test
  @SnapshotName("apiTypeWithFactoryMethods")
  void generate_when_apiTypeWithFactoryMethods_then_matchSnapshot() {
    final Generator<ApiType, PojoSettings> generator =
        ToApiTypeConversion.toApiTypeConversion("field");

    final Writer writer =
        generator.generate(ApiTypes.userId(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("apiTypeWithInstanceMethods")
  void generate_when_apiTypeWithInstanceMethods_then_matchSnapshot() {
    final Generator<ApiType, PojoSettings> generator =
        ToApiTypeConversion.toApiTypeConversion("field");

    final Writer writer =
        generator.generate(ApiTypes.counter(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
