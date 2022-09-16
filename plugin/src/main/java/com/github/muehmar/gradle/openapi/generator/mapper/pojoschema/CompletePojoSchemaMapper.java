package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;

public interface CompletePojoSchemaMapper {
  /** Processes an {@link PojoSchema}. */
  PojoSchemaMapResult process(PojoSchema pojoSchema);

  default PojoSchemaMapResult process(PList<PojoSchema> pojoSchema) {
    return pojoSchema
        .map(this::process)
        .foldRight(PojoSchemaMapResult.empty(), PojoSchemaMapResult::concat);
  }
}
