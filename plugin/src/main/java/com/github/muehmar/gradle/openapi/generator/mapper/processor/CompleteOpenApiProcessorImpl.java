package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;

class CompleteOpenApiProcessorImpl implements CompleteOpenApiProcessor {
  private final SingleSchemaOpenApiProcessor delegate;

  private CompleteOpenApiProcessorImpl(SingleSchemaOpenApiProcessor delegate) {
    this.delegate = delegate;
  }

  public static CompleteOpenApiProcessorImpl ofChainedSingleProcessors(
      SingleSchemaOpenApiProcessor delegate) {
    return new CompleteOpenApiProcessorImpl(delegate);
  }

  @Override
  public SchemaProcessResult process(OpenApiPojo openApiPojo) {
    return delegate
        .process(openApiPojo, this)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + openApiPojo.getSchema()));
  }
}
