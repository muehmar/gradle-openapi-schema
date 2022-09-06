package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

class CompleteOpenApiProcessorImpl implements CompleteOpenApiProcessor {
  private final OpenApiProcessor delegate;

  private CompleteOpenApiProcessorImpl(OpenApiProcessor delegate) {
    this.delegate = delegate;
  }

  public static CompleteOpenApiProcessorImpl ofChained(OpenApiProcessor delegate) {
    return new CompleteOpenApiProcessorImpl(delegate);
  }

  @Override
  public SchemaProcessResult process(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    return delegate
        .process(openApiPojo, pojoSettings, this)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + openApiPojo.getSchema()));
  }
}
