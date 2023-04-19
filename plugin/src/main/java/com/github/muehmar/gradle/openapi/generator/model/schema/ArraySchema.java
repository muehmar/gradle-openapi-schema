package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArraySchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Schema<?> itemsSchema;

  private ArraySchema(Schema<?> delegate, Schema<?> itemsSchema) {
    this.delegate = delegate;
    this.itemsSchema = itemsSchema;
  }

  public static Optional<ArraySchema> wrap(Schema<?> schema) {
    final Schema<?> items = schema.getItems();

    if (items != null && SchemaType.ARRAY.matchesType(schema)) {
      final ArraySchema arraySchema = new ArraySchema(schema, items);
      return Optional.of(arraySchema);
    }

    return Optional.empty();
  }

  public Schema<?> getSchema() {
    return delegate;
  }

  public OpenApiSchema getItemSchema() {
    return OpenApiSchema.wrapSchema(itemsSchema);
  }
}
