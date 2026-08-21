package com.github.muehmar.gradle.openapi.warnings;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarningsHandler {
  private static final Logger logger = LoggerFactory.getLogger(WarningsHandler.class);

  private WarningsHandler() {}

  public static void handleWarnings(
      TaskIdentifier taskIdentifier, FailingWarningTypes failingWarningTypes) {
    final TaskWarnings taskWarnings = WarningsContext.getWarnings(taskIdentifier);
    printWarnings(taskWarnings);
    failOnWarningsIfNecessary(taskWarnings, failingWarningTypes);
  }

  private static void printWarnings(TaskWarnings taskWarnings) {
    final boolean hasWarnings = taskWarnings.hasWarnings();
    if (hasWarnings && logger.isWarnEnabled()) {
      final String formattedWarnings = WarningsFormatter.format(taskWarnings);
      logger.warn(formattedWarnings);
    }
  }

  private static void failOnWarningsIfNecessary(
      TaskWarnings taskWarnings, FailingWarningTypes failingWarningTypes) {
    if (taskWarnings
        .getWarnings()
        .exists(warning -> failingWarningTypes.getTypes().exists(warning.getType()::equals))) {
      throw createException(taskWarnings, failingWarningTypes);
    }
  }

  private static OpenApiGeneratorException createException(
      TaskWarnings taskWarnings, FailingWarningTypes failingWarningTypes) {
    final String formattedWarnings = WarningsFormatter.format(taskWarnings);
    final String message =
        String.format(
            "The generation of the models from the openapi specification failed cause warnings occurred which are configured to fail the generation:\n\n"
                + "%s\n\n"
                + "The following warning types are configured to let the build fail: [%s].\n"
                + "You can either fix the warnings or disable failing on specific warnings in the configuration of the plugin.",
            formattedWarnings, failingWarningTypes.getTypes().mkString(", "));
    return new OpenApiGeneratorException(message);
  }
}
