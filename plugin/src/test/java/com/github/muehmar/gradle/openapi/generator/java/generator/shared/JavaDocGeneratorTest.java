package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.codegenerator.Generator;
import org.junit.jupiter.api.Test;

class JavaDocGeneratorTest {

  @Test
  void javaDoc_when_javaDocWithLineBreaksButAlsoLongLines_then_keepLineBreaksAndAutoNewLine() {
    final Generator<String, Void> generator = JavaDocGenerator.javaDoc();
    final String input =
        "This is some javadoc\n"
            + " 1. Line one\n"
            + " 2. Line two\n"
            + " 3. This is a very long line! This is a very long line! This is a very long line! "
            + "This is a very long line! This is a very long line! This is a very long line! "
            + "This is a very long line! This is a very long line! This is a very long line!";

    final String output = generator.generate(input, noSettings(), javaWriter()).asString();

    assertEquals(
        "/**\n"
            + " * This is some javadoc\n"
            + " *  1. Line one\n"
            + " *  2. Line two\n"
            + " *  3. This is a very long line! This is a very long line! This is a very long\n"
            + " * line! This is a very long line! This is a very long line! This is a very long\n"
            + " * line! This is a very long line! This is a very long line! This is a very long\n"
            + " * line!\n"
            + " */",
        output);
  }

  @Test
  void javaDoc_when_noJavaDocText_then_notOutput() {
    final Generator<String, Void> generator = JavaDocGenerator.javaDoc();

    final String output = generator.generate("", noSettings(), javaWriter()).asString();

    assertEquals("", output);
  }
}
