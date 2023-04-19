package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import java.util.function.Function;

public interface OpenApiSchema {

  static OpenApiSchema wrapSchema(Schema<?> schema) {
    return NonEmptyList.<Function<Schema<?>, Optional<? extends OpenApiSchema>>>single(
            ArraySchema::wrap)
        .add(BooleanSchema::wrap)
        .add(FreeFormSchema::wrap)
        .add(IntegerSchema::wrap)
        .add(NumberSchema::wrap)
        .add(ObjectSchema::wrap)
        .add(ReferenceSchema::wrap)
        .add(StringSchema::wrap)
        .add(MapSchema::wrap)
        .add(NoTypeSchema::wrap)
        .map(f -> f.apply(schema))
        .reduce(Optionals::or)
        .<OpenApiSchema>map(s -> s)
        .orElse(UnknownSchema.wrap(schema));
  }
}
