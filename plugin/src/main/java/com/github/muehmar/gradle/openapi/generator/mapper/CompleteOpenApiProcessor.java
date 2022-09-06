package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public interface CompleteOpenApiProcessor {

  /** Processes an {@link OpenApiPojo}. */
  SchemaProcessResult process(OpenApiPojo openApiPojo, PojoSettings pojoSettings);
}
