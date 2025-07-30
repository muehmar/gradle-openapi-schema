package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrapper;
import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Container holding a key and the corresponding {@link Schema}. */
@EqualsAndHashCode
@ToString
public class PojoSchema {
  private final ComponentName name;
  private final OpenApiSchema schema;

  public PojoSchema(ComponentName name, OpenApiSchema schema) {
    this.name = name;
    this.schema = schema;
  }

  public PojoSchema(ComponentName name, SchemaWrapper wrapper) {
    this(name, OpenApiSchema.wrapSchema(wrapper));
  }

  public ComponentName getName() {
    return name;
  }

  public SchemaName getSchemaName() {
    return name.getSchemaName();
  }

  public PojoName getPojoName() {
    return name.getPojoName();
  }

  public OpenApiSchema getSchema() {
    return schema;
  }

  public MapContext mapToPojo() {
    return schema.mapToPojo(name);
  }
}
