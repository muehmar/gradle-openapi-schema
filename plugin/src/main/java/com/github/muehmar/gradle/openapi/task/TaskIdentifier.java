package com.github.muehmar.gradle.openapi.task;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

/** Identifier of a single gradle schema generation task. */
@EqualsAndHashCode
public class TaskIdentifier implements Serializable {
  private final String identifier;

  private TaskIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public static TaskIdentifier fromString(String identifier) {
    return new TaskIdentifier(identifier);
  }

  @Override
  public String toString() {
    return identifier;
  }
}
