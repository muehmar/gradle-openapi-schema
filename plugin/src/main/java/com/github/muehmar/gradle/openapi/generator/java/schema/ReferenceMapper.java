package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public class ReferenceMapper {
  private ReferenceMapper() {}

  public static JavaType getRefType(PojoSettings pojoSettings, String ref) {
    final int i = ref.lastIndexOf('/');
    return JavaType.ofReference(ref.substring(Math.max(i + 1, 0)), pojoSettings.getSuffix());
  }
}
