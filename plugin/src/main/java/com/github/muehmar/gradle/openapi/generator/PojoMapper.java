package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/** Maps a {@link Schema} with the corresponding key to {@link Pojo}'s. */
@FunctionalInterface
public interface PojoMapper {
  PList<Pojo> fromSchema(PList<OpenApiPojo> openApiPojos, PojoSettings pojoSettings);

  default PList<Pojo> fromSchema(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    return fromSchema(PList.single(openApiPojo), pojoSettings);
  }
}
