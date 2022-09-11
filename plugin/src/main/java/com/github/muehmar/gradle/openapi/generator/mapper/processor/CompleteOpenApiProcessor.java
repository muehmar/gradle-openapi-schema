package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;

public interface CompleteOpenApiProcessor {
  /** Processes an {@link OpenApiPojo}. */
  SchemaProcessResult process(OpenApiPojo openApiPojo);
}
