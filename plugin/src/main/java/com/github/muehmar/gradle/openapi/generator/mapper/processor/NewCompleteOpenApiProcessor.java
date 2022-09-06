package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public interface NewCompleteOpenApiProcessor {
  /** Processes an {@link OpenApiPojo}. */
  NewSchemaProcessResult process(OpenApiPojo openApiPojo, PojoSettings pojoSettings);
}
