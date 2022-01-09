package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
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

    final String output =
        generator.generate(input, noSettings(), Writer.createDefault()).asString();

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

  private static Void noSettings() {
    return null;
  }
}