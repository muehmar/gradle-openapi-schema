package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class BooleanSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private BooleanSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<BooleanSchema> wrap(Schema<?> schema) {
    if (SchemaType.BOOLEAN.matchesType(schema)) {
      final BooleanSchema booleanSchema = new BooleanSchema(schema);
      return Optional.of(booleanSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
