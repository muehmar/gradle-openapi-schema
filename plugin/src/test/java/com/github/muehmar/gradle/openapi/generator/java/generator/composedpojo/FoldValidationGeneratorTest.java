package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class FoldValidationGeneratorTest {
  private Expect expect;

  @Test
  void test() {
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldValidationGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(ComposedPojo.CompositionType.ONE_OF),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void test2() {
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldValidationGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(ComposedPojo.CompositionType.ANY_OF),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
