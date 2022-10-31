package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.mapper.reader.FileSpecificationReader;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SwaggerSpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolverImpl;

public class PojoMapperFactory {
  private PojoMapperFactory() {}

  public static SpecificationMapper create(String pojoSuffix) {
    return SpecificationMapperImpl.create(
        new MapResultResolverImpl(),
        new SwaggerSpecificationParser(new FileSpecificationReader(), pojoSuffix));
  }
}
