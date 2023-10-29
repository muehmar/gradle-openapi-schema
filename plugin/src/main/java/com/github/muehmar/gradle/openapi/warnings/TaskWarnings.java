package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import lombok.Value;

/** Warnings occurred for a specific task. */
@Value
public class TaskWarnings {
  TaskIdentifier taskIdentifier;
  PList<Warning> warnings;

  public boolean hasWarnings() {
    return warnings.nonEmpty();
  }
}
