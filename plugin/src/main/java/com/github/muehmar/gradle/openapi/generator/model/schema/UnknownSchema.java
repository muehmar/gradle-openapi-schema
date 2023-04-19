package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnknownSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private UnknownSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static UnknownSchema wrap(Schema<?> schema) {
    return new UnknownSchema(schema);
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
