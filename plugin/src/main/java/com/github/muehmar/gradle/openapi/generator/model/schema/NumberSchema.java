package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NumberSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private NumberSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<NumberSchema> wrap(Schema<?> schema) {
    if (SchemaType.NUMBER.matchesType(schema)) {
      final NumberSchema numberSchema = new NumberSchema(schema);
      return Optional.of(numberSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
