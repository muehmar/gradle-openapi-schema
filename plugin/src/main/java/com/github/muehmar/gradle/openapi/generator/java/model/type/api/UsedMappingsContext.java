package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.DtoMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context to track which mappings are actually used during code generation to be able to detect
 * unused mappings.
 */
public class UsedMappingsContext {
  private static final Map<TaskIdentifier, UsedMappings> USED_MAPPINGS_BY_TASK =
      new ConcurrentHashMap<>();

  private UsedMappingsContext() {}

  public static void clearForTask(TaskIdentifier taskIdentifier) {
    USED_MAPPINGS_BY_TASK.remove(taskIdentifier);
  }

  public static void recordClassMappingUsage(
      TaskIdentifier taskIdentifier, ClassTypeMapping mapping) {
    USED_MAPPINGS_BY_TASK
        .computeIfAbsent(taskIdentifier, k -> new UsedMappings())
        .classTypeMappings
        .add(mapping);
  }

  public static void recordFormatMappingUsage(
      TaskIdentifier taskIdentifier, FormatTypeMapping mapping) {
    USED_MAPPINGS_BY_TASK
        .computeIfAbsent(taskIdentifier, k -> new UsedMappings())
        .formatTypeMappings
        .add(mapping);
  }

  public static void recordDtoMappingUsage(TaskIdentifier taskIdentifier, DtoMapping mapping) {
    USED_MAPPINGS_BY_TASK
        .computeIfAbsent(taskIdentifier, k -> new UsedMappings())
        .dtoMappings
        .add(mapping);
  }

  public static Set<ClassTypeMapping> getUsedClassMappings(TaskIdentifier taskIdentifier) {
    return Optional.ofNullable(USED_MAPPINGS_BY_TASK.get(taskIdentifier))
        .map(um -> Collections.unmodifiableSet(um.classTypeMappings))
        .orElse(Collections.emptySet());
  }

  public static Set<FormatTypeMapping> getUsedFormatMappings(TaskIdentifier taskIdentifier) {
    return Optional.ofNullable(USED_MAPPINGS_BY_TASK.get(taskIdentifier))
        .map(um -> Collections.unmodifiableSet(um.formatTypeMappings))
        .orElse(Collections.emptySet());
  }

  public static Set<DtoMapping> getUsedDtoMappings(TaskIdentifier taskIdentifier) {
    return Optional.ofNullable(USED_MAPPINGS_BY_TASK.get(taskIdentifier))
        .map(um -> Collections.unmodifiableSet(um.dtoMappings))
        .orElse(Collections.emptySet());
  }

  private static class UsedMappings {
    private final Set<ClassTypeMapping> classTypeMappings =
        Collections.synchronizedSet(new HashSet<>());
    private final Set<FormatTypeMapping> formatTypeMappings =
        Collections.synchronizedSet(new HashSet<>());
    private final Set<DtoMapping> dtoMappings = Collections.synchronizedSet(new HashSet<>());
  }
}
