package com.github.muehmar.gradle.openapi;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Comparator;
import java.util.function.Function;

public class SnapshotUtil {
  private SnapshotUtil() {}

  public static String writerSnapshot(Writer writer) {
    final PList<String> orderedRefs =
        writer
            .getRefs()
            .distinct(Function.identity())
            .sort(Comparator.comparing(Function.identity()));
    final String refs = orderedRefs.mkString("\n");
    return String.format("%s\n\n%s", refs, writer.asString());
  }
}
