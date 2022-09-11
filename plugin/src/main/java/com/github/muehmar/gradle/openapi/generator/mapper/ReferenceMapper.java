package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;

public class ReferenceMapper {
  private ReferenceMapper() {}

  public static Name getRefName(String ref) {
    final int i = ref.lastIndexOf('/');
    return Name.ofString(ref.substring(Math.max(i + 1, 0))).startUpperCase();
  }
}
