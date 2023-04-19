package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FreeFormSchema implements OpenApiSchema {
  private final Schema<?> delegate;

  private FreeFormSchema(Schema<?> delegate) {
    this.delegate = delegate;
  }

  public static Optional<FreeFormSchema> wrap(Schema<?> schema) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (SchemaType.OBJECT.matchesType(schema)
        && schema.getProperties() == null
        && freeFormAdditionalProperties(additionalProperties)) {

      final FreeFormSchema freeFormSchema = new FreeFormSchema(schema);
      return Optional.of(freeFormSchema);
    }

    return Optional.empty();
  }

  private static boolean freeFormAdditionalProperties(Object additionalProperties) {
    return additionalProperties == null
        || Boolean.TRUE.equals(additionalProperties)
        || isNoSchemaAdditionalProperties(additionalProperties);
  }

  private static boolean isNoSchemaAdditionalProperties(Object additionalProperties) {
    if (additionalProperties instanceof Schema) {
      final Schema<?> schema = (Schema<?>) additionalProperties;
      return schema.getProperties() == null
          && schema.getAdditionalProperties() == null
          && schema.get$ref() == null;
    }
    return false;
  }

  public Schema<?> getSchema() {
    return delegate;
  }
}
