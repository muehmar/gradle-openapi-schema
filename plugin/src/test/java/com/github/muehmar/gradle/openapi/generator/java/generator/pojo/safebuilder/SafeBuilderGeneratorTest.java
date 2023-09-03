package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.IntellijDiffSnapshotTestExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith({SnapshotExtension.class, IntellijDiffSnapshotTestExtension.class})
class SafeBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("enabledSafeBuilder")
  void generate_when_enabledSafeBuilder_then_correctOutput(SafeBuilderVariant variant) {
    final SafeBuilderGenerator gen = new SafeBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            defaultTestSettings().withEnableSafeBuilder(true),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("disabledSafeBuilder")
  void generate_when_disabledSafeBuilder_then_correctOutput(SafeBuilderVariant variant) {
    final SafeBuilderGenerator gen = new SafeBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            defaultTestSettings().withEnableSafeBuilder(false),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
