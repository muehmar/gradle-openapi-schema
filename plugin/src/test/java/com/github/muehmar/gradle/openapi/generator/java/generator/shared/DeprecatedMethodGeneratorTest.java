package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.DeprecatedMethodGenerator.deprecatedJavaDocAndAnnotationForValidationMethod;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class DeprecatedMethodGeneratorTest {
  private Expect expect;

  @Test
  void generate_when_disabledDeprecationAnnotation_then_noOutput() {
    final Generator<Object, PojoSettings> generator =
        deprecatedJavaDocAndAnnotationForValidationMethod();

    final Writer writer =
        generator.generate(
            new Object(), TestPojoSettings.defaultTestSettings(), Writer.javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_enabledDeprecationAnnotation_then_matchSnapshot() {
    final Generator<Object, PojoSettings> generator =
        deprecatedJavaDocAndAnnotationForValidationMethod();

    final Writer writer =
        generator.generate(
            new Object(),
            TestPojoSettings.defaultTestSettings()
                .withValidationMethods(new ValidationMethods(JavaModifier.PUBLIC, "Raw", true)),
            Writer.javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
