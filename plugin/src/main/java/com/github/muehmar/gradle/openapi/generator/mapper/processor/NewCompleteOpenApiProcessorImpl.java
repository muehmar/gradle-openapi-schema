package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;

class NewCompleteOpenApiProcessorImpl implements NewCompleteOpenApiProcessor {
  private final SingleSchemaOpenApiProcessor delegate;

  private NewCompleteOpenApiProcessorImpl(SingleSchemaOpenApiProcessor delegate) {
    this.delegate = delegate;
  }

  public static NewCompleteOpenApiProcessorImpl ofChainedSingleProcessors(
      SingleSchemaOpenApiProcessor delegate) {
    return new NewCompleteOpenApiProcessorImpl(delegate);
  }

  @Override
  public NewSchemaProcessResult process(OpenApiPojo openApiPojo) {
    return delegate
        .process(openApiPojo, this)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + openApiPojo.getSchema()));
  }
}
