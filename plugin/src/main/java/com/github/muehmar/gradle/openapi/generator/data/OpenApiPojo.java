package com.github.muehmar.gradle.openapi.generator.data;

import io.swagger.v3.oas.models.media.Schema;

/** Container holding a key and the corresponding {@link Schema}. */
public class OpenApiPojo {
  private final String key;
  private final Schema<?> schema;

  public OpenApiPojo(String key, Schema<?> schema) {
    this.key = key;
    this.schema = schema;
  }

  public String getKey() {
    return key;
  }

  @SuppressWarnings("java:S1452")
  public Schema<?> getSchema() {
    return schema;
  }
}
