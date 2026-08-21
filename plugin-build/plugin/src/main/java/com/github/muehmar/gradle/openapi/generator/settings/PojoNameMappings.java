package com.github.muehmar.gradle.openapi.generator.settings;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Value;

@Value
public class PojoNameMappings implements Serializable {
  private List<ConstantNameMapping> constantNameMappings;

  public static PojoNameMappings noMappings() {
    return new PojoNameMappings(Collections.emptyList());
  }
}
