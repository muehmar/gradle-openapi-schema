package com.github.muehmar.gradle.openapi.generator.settings;

import java.util.Optional;

public class ClassTypeMappings {

  private ClassTypeMappings() {}

  public static final TypeConversion STRING_CONVERSION =
      new TypeConversion("com.custom.CustomString#toString", "com.custom.CustomString#fromString");
  public static final ClassTypeMapping STRING_MAPPING_WITH_CONVERSION =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.of(STRING_CONVERSION));

  public static final TypeConversion LIST_CONVERSION =
      new TypeConversion("com.custom.CustomList#toList", "com.custom.CustomList#fromList");
  public static final ClassTypeMapping LIST_MAPPING_WITH_CONVERSION =
      new ClassTypeMapping("List", "com.custom.CustomList", Optional.of(LIST_CONVERSION));

  public static final TypeConversion MAP_CONVERSION =
      new TypeConversion("com.custom.CustomMap#toMap", "com.custom.CustomMap#fromMap");
  public static final ClassTypeMapping MAP_MAPPING_WITH_CONVERSION =
      new ClassTypeMapping("Map", "com.custom.CustomMap", Optional.of(MAP_CONVERSION));
}
