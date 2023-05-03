package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Container holding a key and the corresponding {@link Schema}. */
@EqualsAndHashCode
@ToString
public class PojoSchema {
  private final PojoName name;
  private final OpenApiSchema schema;

  public PojoSchema(PojoName name, OpenApiSchema schema) {
    this.name = name.startUppercase();
    this.schema = schema;
  }

  public PojoSchema(PojoName name, Schema<?> schema) {
    this(name, OpenApiSchema.wrapSchema(schema));
  }

  public PojoName getPojoName() {
    return name;
  }

  @SuppressWarnings("java:S1452")
  public OpenApiSchema getSchema() {
    return schema;
  }

  public MapContext mapToPojo() {
    return schema.mapToPojo(name);
  }
}
