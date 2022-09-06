package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

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
  public NewSchemaProcessResult process(OpenApiPojo openApiPojo, PojoSettings pojoSettings) {
    return delegate
        .process(openApiPojo, pojoSettings, this)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "The following schema is currently not supported as root schema in the components section (contact the maintainer to support this schema as well): "
                        + openApiPojo.getSchema()));
  }
}
