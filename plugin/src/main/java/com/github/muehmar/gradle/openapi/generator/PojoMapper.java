package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/** Maps a {@link Schema} with the corresponding key to {@link Pojo}'s. */
@FunctionalInterface
public interface PojoMapper {
  PList<Pojo> fromSchemas(PList<OpenApiPojo> openApiPojos, PojoSettings pojoSettings);

  default PList<Pojo> fromSchemas(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    return fromSchemas(PList.single(openApiPojo), pojoSettings);
  }
}
