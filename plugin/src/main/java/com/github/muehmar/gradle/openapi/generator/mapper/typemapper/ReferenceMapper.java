package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;

public class ReferenceMapper {
  private ReferenceMapper() {}

  public static PojoName getPojoName(String ref, String suffix) {
    return PojoName.ofNameAndSuffix(getRefName(ref), suffix);
  }

  public static Name getRefName(String ref) {
    final int i = ref.lastIndexOf('/');
    return Name.of(ref.substring(Math.max(i + 1, 0))).startUpperCase();
  }
}
