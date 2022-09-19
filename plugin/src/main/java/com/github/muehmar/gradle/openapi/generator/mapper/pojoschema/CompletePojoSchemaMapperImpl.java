package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;

class CompletePojoSchemaMapperImpl implements CompletePojoSchemaMapper {
  private final SinglePojoSchemaMapper delegate;

  private CompletePojoSchemaMapperImpl(SinglePojoSchemaMapper delegate) {
    this.delegate = delegate;
  }

  public static CompletePojoSchemaMapperImpl ofChainedSingleProcessors(
      SinglePojoSchemaMapper delegate) {
    return new CompletePojoSchemaMapperImpl(delegate);
  }

  @Override
  public MapContext map(PojoSchema pojoSchema) {
    return delegate
        .map(pojoSchema)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + pojoSchema.getSchema()));
  }
}
