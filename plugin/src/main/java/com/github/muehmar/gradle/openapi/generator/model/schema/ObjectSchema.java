package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ObjectSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Map<String, Schema> properties;

  private ObjectSchema(Schema<?> delegate, Map<String, Schema> properties) {
    this.delegate = delegate;
    this.properties = properties;
  }

  public static Optional<ObjectSchema> wrap(Schema<?> schema) {
    final Map<String, Schema> properties = schema.getProperties();
    if (SchemaType.OBJECT.matchesType(schema) && properties != null) {

      final ObjectSchema objectSchema = new ObjectSchema(schema, properties);
      return Optional.of(objectSchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
