package com.github.muehmar.gradle.openapi.warnings;

import com.github.muehmar.gradle.openapi.task.TaskIdentifier;

public class WarningsFormatter {
  private WarningsFormatter() {}

  public static String format(TaskIdentifier identifier, Warnings warnings) {
    return warnings
        .getWarnings(identifier)
        .map(warning -> String.format("%s - %s", warning.getType(), warning.getMessage()))
        .mkString("\n");
  }
}
