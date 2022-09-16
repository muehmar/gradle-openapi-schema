package com.github.muehmar.gradle.openapi.generator.mapper;

public class PojoMapperFactory {
  private PojoMapperFactory() {}

  public static PojoMapper create() {
    return PojoMapperImpl.create();
  }
}
