package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;

public interface CompletePojoSchemaMapper {
  /** Processes an {@link PojoSchema}. */
  MapContext map(PojoSchema pojoSchema);
}
