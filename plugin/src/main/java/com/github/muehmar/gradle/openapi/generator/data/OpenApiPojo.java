package com.github.muehmar.gradle.openapi.generator.data;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenApiPojo that = (OpenApiPojo) o;
    return Objects.equals(key, that.key) && Objects.equals(schema, that.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, schema);
  }

  @Override
  public String toString() {
    return "OpenApiPojo{" + "key='" + key + '\'' + ", schema=" + schema + '}';
  }
}
