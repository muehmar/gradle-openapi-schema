package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import io.swagger.v3.oas.models.media.Schema;

/** Maps a {@link Schema} with the corresponding key to {@link Pojo}'s. */
@FunctionalInterface
public interface PojoMapper {
  PList<Pojo> fromSchemas(PList<PojoSchema> openApiPojos);

  default PList<Pojo> fromSchemas(PojoSchema pojoSchema) {
    return fromSchemas(PList.single(pojoSchema));
  }
}
