package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiTypes;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class ToApiTypeConversionTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("apiTypeWithFactoryMethods")
  void generate_when_apiTypeWithFactoryMethods_then_matchSnapshot(ConversionGenerationMode mode) {
    final Generator<ApiType, Void> generator =
        ToApiTypeConversion.toApiTypeConversion("field", mode);

    final Writer writer = generator.generate(ApiTypes.userId(), noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("apiTypeWithInstanceMethods")
  void generate_when_apiTypeWithInstanceMethods_then_matchSnapshot(ConversionGenerationMode mode) {
    final Generator<ApiType, Void> generator =
        ToApiTypeConversion.toApiTypeConversion("field", mode);

    final Writer writer = generator.generate(ApiTypes.counter(), noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
