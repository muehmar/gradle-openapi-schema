package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.mapper.reader.FileSpecificationReader;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SwaggerSpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolverImpl;

public class PojoMapperFactory {
  private PojoMapperFactory() {}

  public static PojoMapper create(String pojoSuffix) {
    return PojoMapperImpl.create(
        new MapResultResolverImpl(),
        new SwaggerSpecificationParser(new FileSpecificationReader(), pojoSuffix));
  }
}
