package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Global container for collecting the {@link Warning}'s occurred during the generation. */
public class WarningsContext {
  private static final Map<TaskIdentifier, Set<Warning>> map = new ConcurrentHashMap<>();

  private WarningsContext() {}

  public static void addWarningForTask(TaskIdentifier identifier, Warning warning) {
    final Set<Warning> warnings =
        new HashSet<>(map.getOrDefault(identifier, Collections.emptySet()));
    warnings.add(warning);
    map.put(identifier, Collections.unmodifiableSet(warnings));
  }

  public static TaskWarnings getWarnings(TaskIdentifier identifier) {
    final PList<Warning> warnings =
        PList.fromIter(map.getOrDefault(identifier, Collections.emptySet()));
    return new TaskWarnings(identifier, warnings);
  }
}
