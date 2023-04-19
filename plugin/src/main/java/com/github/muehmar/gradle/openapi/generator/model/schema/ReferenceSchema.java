package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReferenceSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final String reference;

  private ReferenceSchema(Schema<?> delegate, String reference) {
    this.delegate = delegate;
    this.reference = reference;
  }

  public static Optional<ReferenceSchema> wrap(Schema<?> schema) {
    if (schema.getType() == null && schema.getFormat() == null && schema.get$ref() != null) {
      final ReferenceSchema referenceSchema = new ReferenceSchema(schema, schema.get$ref());
      return Optional.of(referenceSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
