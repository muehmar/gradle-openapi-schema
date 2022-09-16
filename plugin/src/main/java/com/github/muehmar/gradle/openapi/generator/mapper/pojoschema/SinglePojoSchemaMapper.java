package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.Optional;

public interface SinglePojoSchemaMapper {
  /**
   * Processes an {@link PojoSchema}. If this processor is not capable of processing the
   * corresponding schema an empty {@link Optional} will get returned.
   */
  Optional<PojoSchemaMapResult> map(
      PojoSchema pojoSchema, CompletePojoSchemaMapper completePojoSchemaMapper);

  default SinglePojoSchemaMapper or(SinglePojoSchemaMapper next) {
    final SinglePojoSchemaMapper self = this;
    return (pojoSchema, completeOpenApiProcessor) ->
        Optionals.or(
            self.map(pojoSchema, completeOpenApiProcessor),
            next.map(pojoSchema, completeOpenApiProcessor));
  }

  default CompletePojoSchemaMapper orLast(SinglePojoSchemaMapper next) {
    final SinglePojoSchemaMapper openApiProcessor = or(next);

    return CompletePojoSchemaMapperImpl.ofChainedSingleProcessors(openApiProcessor);
  }
}
