package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JavaDocGeneratorsTest {

  private Expect expect;

  @Test
  void generate_when_disabledDeprecationWarning_then_noOutput() {
    final Generator<String, PojoSettings> gen =
        JavaDocGenerators.deprecatedValidationMethodJavaDoc();

    final Writer writer = gen.generate("", defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_enabledDeprecationWarning_then_correctOutput() {
    final Generator<String, PojoSettings> gen =
        JavaDocGenerators.deprecatedValidationMethodJavaDoc();

    final Writer writer =
        gen.generate(
            "",
            defaultTestSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods().withDeprecatedAnnotation(true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
