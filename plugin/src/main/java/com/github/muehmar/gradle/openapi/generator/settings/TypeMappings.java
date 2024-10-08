package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import lombok.Value;

@Value
public class TypeMappings {
  PList<ClassTypeMapping> classTypeMappings;
  PList<FormatTypeMapping> formatTypeMappings;

  public static TypeMappings empty() {
    return new TypeMappings(PList.empty(), PList.empty());
  }

  public static TypeMappings ofSingleClassTypeMapping(ClassTypeMapping classTypeMapping) {
    return new TypeMappings(PList.single(classTypeMapping), PList.empty());
  }

  public static TypeMappings ofClassTypeMappings(ClassTypeMapping... classTypeMapping) {
    return new TypeMappings(PList.fromArray(classTypeMapping), PList.empty());
  }

  public static TypeMappings ofSingleFormatTypeMapping(FormatTypeMapping formatTypeMapping) {
    return new TypeMappings(PList.empty(), PList.single(formatTypeMapping));
  }
}
