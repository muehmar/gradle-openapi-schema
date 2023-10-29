package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Warnings {
  private static final Warnings INSTANCE = new Warnings();
  private final Map<TaskIdentifier, PList<Warning>> map;

  private Warnings() {
    map = new ConcurrentHashMap<>();
  }

  public static Warnings getInstance() {
    return INSTANCE;
  }

  public void add(TaskIdentifier identifier, Warning warning) {
    final PList<Warning> list = map.getOrDefault(identifier, PList.empty());
    map.put(identifier, list.add(warning));
  }

  public boolean hasWarningsForTask(TaskIdentifier identifier) {
    return getWarnings(identifier).nonEmpty();
  }

  public PList<Warning> getWarnings(TaskIdentifier identifier) {
    return map.getOrDefault(identifier, PList.empty());
  }
}
