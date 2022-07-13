package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class AnnotationGeneratorTest {

  @Test
  void deprecatedValidationGetter_when_disabledAnnotation_then_notOutput() {
    final Generator<Void, PojoSettings> generator =
        AnnotationGenerator.deprecatedValidationGetter();
    final PojoSettings settings = TestPojoSettings.defaultSettings();
    final Writer writer = generator.generate(noData(), settings, Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void deprecatedValidationGetter_when_enabledAnnotation_then_deprecatedAnnotation() {
    final Generator<Void, PojoSettings> generator =
        AnnotationGenerator.deprecatedValidationGetter();
    final PojoSettings settings =
        TestPojoSettings.defaultSettings()
            .withValidationGetter(
                TestPojoSettings.defaultValidationGetter().withDeprecatedAnnotation(true));

    final Writer writer = generator.generate(noData(), settings, Writer.createDefault());

    assertEquals("@Deprecated", writer.asString());
  }
}
