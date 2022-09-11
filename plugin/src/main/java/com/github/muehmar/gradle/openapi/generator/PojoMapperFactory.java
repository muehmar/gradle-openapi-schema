package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.mapper.processor.PojoMapperImpl;

public class PojoMapperFactory {
  private PojoMapperFactory() {}

  public static PojoMapper create() {
    return PojoMapperImpl.create();
  }
}
