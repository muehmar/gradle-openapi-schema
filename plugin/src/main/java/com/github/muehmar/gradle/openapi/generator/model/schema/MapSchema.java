package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Schema<?> additionalProperties;

  private MapSchema(Schema<?> delegate, Schema<?> additionalProperties) {
    this.delegate = delegate;
    this.additionalProperties = additionalProperties;
  }

  public static Optional<MapSchema> wrap(Schema<?> schema) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (SchemaType.OBJECT.matchesType(schema) && additionalProperties instanceof Schema) {

      final MapSchema mapSchema = new MapSchema(schema, (Schema<?>) additionalProperties);
      return Optional.of(mapSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
