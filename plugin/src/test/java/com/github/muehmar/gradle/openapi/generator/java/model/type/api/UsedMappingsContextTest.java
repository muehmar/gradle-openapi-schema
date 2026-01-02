package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappings.DTO_MAPPING_WITHOUT_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappings.DTO_MAPPING_WITH_CONVERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UsedMappingsContextTest {

  private static final ClassTypeMapping CLASS_MAPPING_1 =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.empty());

  private static final TypeConversion CONVERSION =
      new TypeConversion("toString", "com.custom.Id#fromString");
  private static final ClassTypeMapping CLASS_MAPPING_2 =
      new ClassTypeMapping("Long", "com.custom.CustomLong", Optional.of(CONVERSION));

  private static final FormatTypeMapping FORMAT_MAPPING_1 =
      new FormatTypeMapping("uuid", "com.custom.Uuid", Optional.empty());

  private static final FormatTypeMapping FORMAT_MAPPING_2 =
      new FormatTypeMapping("email", "com.custom.Email", Optional.of(CONVERSION));

  @Test
  void recordClassMappingUsage_when_singleMapping_then_recordedCorrectly() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordClassMappingUsage(taskId, CLASS_MAPPING_1);

    assertEquals(Set.of(CLASS_MAPPING_1), UsedMappingsContext.getUsedClassMappings(taskId));
  }

  @Test
  void recordClassMappingUsage_when_multipleMappings_then_allRecorded() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordClassMappingUsage(taskId, CLASS_MAPPING_1);
    UsedMappingsContext.recordClassMappingUsage(taskId, CLASS_MAPPING_2);

    assertEquals(
        Set.of(CLASS_MAPPING_1, CLASS_MAPPING_2), UsedMappingsContext.getUsedClassMappings(taskId));
  }

  @Test
  void recordFormatMappingUsage_when_singleMapping_then_recordedCorrectly() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordFormatMappingUsage(taskId, FORMAT_MAPPING_1);

    assertEquals(Set.of(FORMAT_MAPPING_1), UsedMappingsContext.getUsedFormatMappings(taskId));
  }

  @Test
  void recordFormatMappingUsage_when_multipleMappings_then_allRecorded() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordFormatMappingUsage(taskId, FORMAT_MAPPING_1);
    UsedMappingsContext.recordFormatMappingUsage(taskId, FORMAT_MAPPING_2);

    assertEquals(
        Set.of(FORMAT_MAPPING_1, FORMAT_MAPPING_2),
        UsedMappingsContext.getUsedFormatMappings(taskId));
  }

  @Test
  void recordDtoMappingUsage_when_singleMapping_then_recordedCorrectly() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordDtoMappingUsage(taskId, DTO_MAPPING_WITH_CONVERSION);

    assertEquals(
        Set.of(DTO_MAPPING_WITH_CONVERSION), UsedMappingsContext.getUsedDtoMappings(taskId));
  }

  @Test
  void recordDtoMappingUsage_when_multipleMappings_then_allRecorded() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordDtoMappingUsage(taskId, DTO_MAPPING_WITH_CONVERSION);
    UsedMappingsContext.recordDtoMappingUsage(taskId, DTO_MAPPING_WITHOUT_CONVERSION);

    assertEquals(
        Set.of(DTO_MAPPING_WITH_CONVERSION, DTO_MAPPING_WITHOUT_CONVERSION),
        UsedMappingsContext.getUsedDtoMappings(taskId));
  }

  @Test
  void getUsedClassMappings_when_noMappingsRecorded_then_returnEmptySet() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    assertEquals(Set.of(), UsedMappingsContext.getUsedClassMappings(taskId));
  }

  @Test
  void getUsedFormatMappings_when_noMappingsRecorded_then_returnEmptySet() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    assertEquals(Set.of(), UsedMappingsContext.getUsedFormatMappings(taskId));
  }

  @Test
  void getUsedDtoMappings_when_noMappingsRecorded_then_returnEmptySet() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    assertEquals(Set.of(), UsedMappingsContext.getUsedDtoMappings(taskId));
  }

  @Test
  void clearForTask_when_mappingsRecorded_then_mappingsCleared() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordClassMappingUsage(taskId, CLASS_MAPPING_1);
    UsedMappingsContext.recordFormatMappingUsage(taskId, FORMAT_MAPPING_1);
    UsedMappingsContext.recordDtoMappingUsage(taskId, DTO_MAPPING_WITH_CONVERSION);

    UsedMappingsContext.clearForTask(taskId);

    assertEquals(Set.of(), UsedMappingsContext.getUsedClassMappings(taskId));
    assertEquals(Set.of(), UsedMappingsContext.getUsedFormatMappings(taskId));
    assertEquals(Set.of(), UsedMappingsContext.getUsedDtoMappings(taskId));
  }

  @Test
  void recordMapping_when_differentTasks_then_mappingsIsolated() {
    final TaskIdentifier task1 = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final TaskIdentifier task2 = TaskIdentifier.fromString(UUID.randomUUID().toString());

    UsedMappingsContext.recordClassMappingUsage(task1, CLASS_MAPPING_1);
    UsedMappingsContext.recordFormatMappingUsage(task2, FORMAT_MAPPING_1);

    assertEquals(Set.of(CLASS_MAPPING_1), UsedMappingsContext.getUsedClassMappings(task1));
    assertEquals(Set.of(), UsedMappingsContext.getUsedFormatMappings(task1));

    assertEquals(Set.of(), UsedMappingsContext.getUsedClassMappings(task2));
    assertEquals(Set.of(FORMAT_MAPPING_1), UsedMappingsContext.getUsedFormatMappings(task2));
  }
}
