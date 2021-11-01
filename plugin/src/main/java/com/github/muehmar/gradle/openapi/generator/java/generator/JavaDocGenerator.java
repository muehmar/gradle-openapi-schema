package com.github.muehmar.gradle.openapi.generator.java.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class JavaDocGenerator {
  private static final int MAX_LENGTH = 80;

  private JavaDocGenerator() {}

  public static Generator<String, Void> javaDoc() {
    return Generator.<String, Void>ofWriterFunction(w -> w.println("/**"))
        .append(content())
        .append(w -> w.println(" */"));
  }

  private static Generator<String, Void> content() {
    return (input, ign, writer) ->
        PList.fromArray(input.split("\n"))
            .flatMap(JavaDocGenerator::autoNewline)
            .map(line -> " * " + line)
            .foldLeft(writer, Writer::println);
  }

  private static PList<String> autoNewline(String line) {
    if (line.length() <= MAX_LENGTH) {
      return PList.single(line);
    }

    final int lastWhiteSpaceWithinMaxLength = line.substring(0, 80).lastIndexOf(" ");
    final int firstWhitespace = line.indexOf(" ");

    final int firstWhitespaceOrLineLength = firstWhitespace > 0 ? firstWhitespace : line.length();
    final int newLineIndex =
        lastWhiteSpaceWithinMaxLength > 0
            ? lastWhiteSpaceWithinMaxLength
            : firstWhitespaceOrLineLength;

    if (newLineIndex >= line.length() - 2) {
      return PList.single(line);
    }

    final String firstLine = line.substring(0, newLineIndex);
    final String remaining = line.substring(newLineIndex + 1);
    return autoNewline(remaining).cons(firstLine);
  }
}
