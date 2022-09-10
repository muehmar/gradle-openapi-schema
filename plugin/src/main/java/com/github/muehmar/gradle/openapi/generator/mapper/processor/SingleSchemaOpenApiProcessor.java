package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.Optional;

public interface SingleSchemaOpenApiProcessor {
  /**
   * Processes an {@link OpenApiPojo}. If this processor is not capable of processing the
   * corresponding schema an empty {@link Optional} will get returned.
   */
  Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo, NewCompleteOpenApiProcessor completeOpenApiProcessor);

  default SingleSchemaOpenApiProcessor or(SingleSchemaOpenApiProcessor next) {
    final SingleSchemaOpenApiProcessor self = this;
    return (openApiPojo, completeOpenApiProcessor) ->
        Optionals.or(
            self.process(openApiPojo, completeOpenApiProcessor),
            next.process(openApiPojo, completeOpenApiProcessor));
  }

  default NewCompleteOpenApiProcessor orLast(SingleSchemaOpenApiProcessor next) {
    final SingleSchemaOpenApiProcessor openApiProcessor = or(next);

    return NewCompleteOpenApiProcessorImpl.ofChainedSingleProcessors(openApiProcessor);
  }
}