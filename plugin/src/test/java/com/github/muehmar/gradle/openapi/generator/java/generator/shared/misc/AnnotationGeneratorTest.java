package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class AnnotationGeneratorTest {

  @Test
  void deprecatedValidationMethod_when_disabledAnnotation_then_notOutput() {
    final Generator<Void, PojoSettings> generator =
        AnnotationGenerator.deprecatedAnnotationForValidationMethod();
    final Writer writer = generator.generate(noData(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void deprecatedValidationMethod_when_enabledAnnotation_then_deprecatedAnnotation() {
    final Generator<Void, PojoSettings> generator =
        AnnotationGenerator.deprecatedAnnotationForValidationMethod();
    final PojoSettings settings =
        defaultTestSettings()
            .withValidationMethods(
                TestPojoSettings.defaultValidationMethods().withDeprecatedAnnotation(true));

    final Writer writer = generator.generate(noData(), settings, javaWriter());

    assertEquals("@Deprecated", writer.asString());
  }
}
