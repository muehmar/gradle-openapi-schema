package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IntegerSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private IntegerSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<IntegerSchema> wrap(Schema<?> schema) {
    if (SchemaType.INTEGER.matchesType(schema)) {
      final IntegerSchema integerSchema = new IntegerSchema(schema);
      return Optional.of(integerSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
