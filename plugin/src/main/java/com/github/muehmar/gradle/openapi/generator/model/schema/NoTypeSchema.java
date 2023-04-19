package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NoTypeSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private NoTypeSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<NoTypeSchema> wrap(Schema<?> schema) {
    if (schema.getType() == null
        && schema.getTypes() == null
        && schema.getFormat() == null
        && schema.get$ref() == null) {

      final NoTypeSchema noTypeSchema = new NoTypeSchema(schema);
      return Optional.of(noTypeSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
