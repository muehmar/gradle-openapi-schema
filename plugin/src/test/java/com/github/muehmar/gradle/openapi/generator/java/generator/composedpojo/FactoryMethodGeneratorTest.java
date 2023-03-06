package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SnapshotExtension.class)
class FactoryMethodGeneratorTest extends JavaComposedPojoVariantsTest {
  private Expect expect;

  @ParameterizedTest
  @SnapshotName("ComposedPojoGenerator")
  @MethodSource("variants")
  void generate_when_composedPojo_then_matchSnapshot(
      ComposedPojo.CompositionType type,
      Optional<Discriminator> discriminator,
      JavaComposedPojo pojo) {
    final Generator<JavaComposedPojo, PojoSettings> generator = FactoryMethodGenerator.generator();

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    compareSnapshot(expect, type, discriminator, pojo, writer);
  }
}
