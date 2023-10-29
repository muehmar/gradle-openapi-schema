package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Global container for collecting the {@link Warning}'s occurred during the generation. */
public class WarningsContext {
  private static final Map<TaskIdentifier, PList<Warning>> map = new ConcurrentHashMap<>();

  private WarningsContext() {}

  public static void addWarningForTask(TaskIdentifier identifier, Warning warning) {
    final PList<Warning> list = map.getOrDefault(identifier, PList.empty());
    map.put(identifier, list.add(warning));
  }

  public static TaskWarnings getWarnings(TaskIdentifier identifier) {
    final PList<Warning> warnings = map.getOrDefault(identifier, PList.empty());
    return new TaskWarnings(identifier, warnings);
  }
}
