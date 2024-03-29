package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.ref.JacksonRefs;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class JacksonNullContainerGeneratorTest {
  @Test
  void containerClass_when_called_then_outputAndRefsCorrect() {
    final Generator<Void, Void> generator = JacksonNullContainerGenerator.containerClass();
    final Writer writer = generator.generate(noData(), noSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));
    assertEquals(
        "package com.github.muehmar.openapi.util;\n"
            + "\n"
            + "import com.fasterxml.jackson.annotation.JsonValue;\n"
            + "\n"
            + "public class JacksonNullContainer<T> {\n"
            + "  private final T value;\n"
            + "\n"
            + "  public JacksonNullContainer(T value) {\n"
            + "    this.value = value;\n"
            + "  }\n"
            + "\n"
            + "  @JsonValue\n"
            + "  public T getValue() {\n"
            + "    return value;\n"
            + "  }\n"
            + "}",
        writer.asString());
  }
}
