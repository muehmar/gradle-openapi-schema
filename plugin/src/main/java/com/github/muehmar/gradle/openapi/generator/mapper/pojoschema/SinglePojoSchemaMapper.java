package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.Optional;

public interface SinglePojoSchemaMapper {
  /**
   * Processes an {@link PojoSchema}. If this processor is not capable of processing the
   * corresponding schema an empty {@link Optional} will get returned.
   */
  Optional<MapContext> map(PojoSchema pojoSchema);

  default SinglePojoSchemaMapper or(SinglePojoSchemaMapper next) {
    final SinglePojoSchemaMapper self = this;
    return (pojoSchema) -> Optionals.or(self.map(pojoSchema), next.map(pojoSchema));
  }

  default CompletePojoSchemaMapper orLast(SinglePojoSchemaMapper next) {
    final SinglePojoSchemaMapper openApiProcessor = or(next);

    return CompletePojoSchemaMapperImpl.ofChainedSingleProcessors(openApiProcessor);
  }
}
