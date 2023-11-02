package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import java.util.Comparator;

public class WarningsFormatter {
  private WarningsFormatter() {}

  public static String format(TaskWarnings taskWarnings) {
    return formatWarnings(taskWarnings.getWarnings());
  }

  private static String formatWarnings(PList<Warning> warnings) {
    return warnings
        .sort(Comparator.comparing(Warning::getType))
        .map(WarningsFormatter::formatWarning)
        .mkString("\n");
  }

  private static String formatWarning(Warning warning) {
    return String.format("* Warning: %s - %s", warning.getType(), warning.getMessage());
  }
}
