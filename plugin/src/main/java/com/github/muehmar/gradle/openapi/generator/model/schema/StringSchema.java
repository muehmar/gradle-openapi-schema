package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StringSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Optional<String> format;

  private StringSchema(Schema<?> delegate, Optional<String> format) {
    this.delegate = delegate;
    this.format = format;
  }

  public static Optional<StringSchema> wrap(Schema<?> schema) {
    if (SchemaType.STRING.matchesType(schema)) {
      final StringSchema stringSchema =
          new StringSchema(schema, Optional.ofNullable(schema.getFormat()));
      return Optional.of(stringSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
