package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Container holding a key and the corresponding {@link Schema}. */
@EqualsAndHashCode
@ToString
public class PojoSchema {
  private final PojoName name;
  private final Schema<?> schema;

  public PojoSchema(PojoName name, Schema<?> schema) {
    this.name = name.startUppercase();
    this.schema = schema;
  }

  public PojoName getPojoName() {
    return name;
  }

  @SuppressWarnings("java:S1452")
  public Schema<?> getSchema() {
    return schema;
  }
}
