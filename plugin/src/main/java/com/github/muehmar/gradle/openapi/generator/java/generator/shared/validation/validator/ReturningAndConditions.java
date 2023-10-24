package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.codegenerator.util.Strings;
import io.github.muehmar.codegenerator.writer.Writer;
import lombok.Value;

@Value
public class ReturningAndConditions {
  Writer writer;

  public static ReturningAndConditions forConditions(PList<Writer> writers) {
    final NonEmptyList<String> formattedConditions =
        NonEmptyList.fromIter(writers.map(Writer::asString).filter(Strings::nonEmptyOrBlank))
            .orElse(NonEmptyList.single("true"));

    final String firstFormatted = formattedConditions.head();
    final PList<String> remainingFormatted = formattedConditions.tail();

    final PList<String> refs = writers.flatMap(Writer::getRefs);

    if (remainingFormatted.isEmpty()) {
      final Writer writer = javaWriter().println("return %s;", firstFormatted).refs(refs);
      return new ReturningAndConditions(writer);
    } else {
      final Writer writer =
          remainingFormatted
              .foldLeft(
                  javaWriter().print("return %s", firstFormatted),
                  (w, f) -> w.println().tab(2).print("&& %s", f))
              .println(";")
              .refs(refs);
      return new ReturningAndConditions(writer);
    }
  }
}
