package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiFunction;

public class JavaDocGenerator {
  private static final int MAX_LENGTH = 80;

  private JavaDocGenerator() {}

  public static <A, B> Generator<A, B> javaDoc(BiFunction<A, B, String> javaDoc) {
    return (a, b, writer) -> javaDoc().generate(javaDoc.apply(a, b), (Void) null, writer);
  }

  public static <A, B> Generator<A, B> ofJavaDocString(String javaDoc) {
    return (a, b, writer) -> javaDoc().generate(javaDoc, (Void) null, writer);
  }

  public static <B> Generator<String, B> javaDoc() {
    return Generator.<String, B>ofWriterFunction(w -> w.println("/**"))
        .append(content())
        .append(w -> w.println(" */"))
        .filter(javadoc -> not(javadoc.trim().isEmpty()));
  }

  private static <B> Generator<String, B> content() {
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
