package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class JavaDocGeneratorsTest {

  private Expect expect;

  @Test
  void generate_when_disabledDeprecationWarning_then_noOutput() {
    final Generator<String, PojoSettings> gen =
        JavaDocGenerators.deprecatedValidationMethodJavaDoc();

    final Writer writer =
        gen.generate("", TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_enabledDeprecationWarning_then_correctOutput() {
    final Generator<String, PojoSettings> gen =
        JavaDocGenerators.deprecatedValidationMethodJavaDoc();

    final Writer writer =
        gen.generate(
            "",
            TestPojoSettings.defaultSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods().withDeprecatedAnnotation(true)),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
