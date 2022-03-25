package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ValidationGeneratorTest {
  @Test
  void notNull_when_enabledValidation_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = ValidationGenerator.notNull();

    final Writer writer =
        generator.generate(noData(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.NOT_NULL::equals));
    assertEquals("@NotNull", writer.asString());
  }

  @Test
  void notNull_when_disabledValidation_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = ValidationGenerator.notNull();

    final Writer writer =
        generator.generate(
            noData(),
            TestPojoSettings.defaultSettings().withEnableConstraints(false),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }
}
