package com.github.muehmar.gradle.openapi.generator;

/** Container holding an implementation of {@link PojoMapper} and {@link PojoGenerator}. */
public class OpenApiGenerator {
  private final PojoMapper mapper;
  private final PojoGenerator generator;

  public OpenApiGenerator(PojoMapper mapper, PojoGenerator generator) {
    this.mapper = mapper;
    this.generator = generator;
  }

  public PojoMapper getMapper() {
    return mapper;
  }

  public PojoGenerator getGenerator() {
    return generator;
  }
}
