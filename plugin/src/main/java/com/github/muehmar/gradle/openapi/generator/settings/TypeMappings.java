package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import lombok.Value;

@Value
public class TypeMappings {
  PList<ClassTypeMapping> classTypeMappings;
  PList<FormatTypeMapping> formatTypeMappings;
  PList<DtoMapping> dtoMappings;
  boolean allowNullableForEnums;
  TaskIdentifier taskIdentifier;

  public static TypeMappings empty() {
    return new TypeMappings(
        PList.empty(), PList.empty(), PList.empty(), false, TaskIdentifier.fromString("empty"));
  }

  public static TypeMappings ofSingleClassTypeMapping(
      ClassTypeMapping classTypeMapping, TaskIdentifier taskIdentifier) {
    return new TypeMappings(
        PList.single(classTypeMapping), PList.empty(), PList.empty(), false, taskIdentifier);
  }

  public static TypeMappings ofClassTypeMappings(
      TaskIdentifier taskIdentifier, ClassTypeMapping... classTypeMapping) {
    return new TypeMappings(
        PList.fromArray(classTypeMapping), PList.empty(), PList.empty(), false, taskIdentifier);
  }

  public static TypeMappings ofSingleFormatTypeMapping(
      FormatTypeMapping formatTypeMapping, TaskIdentifier taskIdentifier) {
    return new TypeMappings(
        PList.empty(), PList.single(formatTypeMapping), PList.empty(), false, taskIdentifier);
  }

  public static TypeMappings ofSingleDtoMapping(
      DtoMapping dtoMapping, TaskIdentifier taskIdentifier) {
    return new TypeMappings(
        PList.empty(), PList.empty(), PList.single(dtoMapping), false, taskIdentifier);
  }
}
