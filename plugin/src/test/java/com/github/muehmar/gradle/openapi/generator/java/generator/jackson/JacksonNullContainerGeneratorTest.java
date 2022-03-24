package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class JacksonNullContainerGeneratorTest {
  @Test
  void containerClass_when_called_then_outputAndRefsCorrect() {
    final Generator<Void, Void> generator = JacksonNullContainerGenerator.containerClass();
    final Writer writer = generator.generate(noData(), noSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));
    assertEquals(
        "public static class JacksonNullContainer<T> {\n"
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
