package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.Optional;

public interface SingleSchemaOpenApiProcessor {
  /**
   * Processes an {@link OpenApiPojo}. If this processor is not capable of processing the
   * corresponding schema an empty {@link Optional} will get returned.
   */
  Optional<SchemaProcessResult> process(
      OpenApiPojo openApiPojo, CompleteOpenApiProcessor completeOpenApiProcessor);

  default SingleSchemaOpenApiProcessor or(SingleSchemaOpenApiProcessor next) {
    final SingleSchemaOpenApiProcessor self = this;
    return (openApiPojo, completeOpenApiProcessor) ->
        Optionals.or(
            self.process(openApiPojo, completeOpenApiProcessor),
            next.process(openApiPojo, completeOpenApiProcessor));
  }

  default CompleteOpenApiProcessor orLast(SingleSchemaOpenApiProcessor next) {
    final SingleSchemaOpenApiProcessor openApiProcessor = or(next);

    return CompleteOpenApiProcessorImpl.ofChainedSingleProcessors(openApiProcessor);
  }
}
