package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class PackageGeneratorTest {
  @Test
  void generate_when_called_then_correctOutput() {
    final PackageGenerator<String> generator = new PackageGenerator<>();

    final Writer writer = generator.generate("Hello", defaultTestSettings(), javaWriter());

    assertEquals("package com.github.muehmar;", writer.asString());
  }
}
