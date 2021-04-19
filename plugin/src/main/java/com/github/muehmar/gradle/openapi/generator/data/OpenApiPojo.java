package com.github.muehmar.gradle.openapi.generator.data;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;

/** Container holding a key and the corresponding {@link Schema}. */
public class OpenApiPojo {
  private final Name name;
  private final Schema<?> schema;

  public OpenApiPojo(Name name, Schema<?> schema) {
    this.name = name;
    this.schema = schema;
  }

  public Name getName() {
    return name;
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
    return Objects.equals(name, that.name) && Objects.equals(schema, that.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, schema);
  }

  @Override
  public String toString() {
    return "OpenApiPojo{" + "name='" + name + '\'' + ", schema=" + schema + '}';
  }
}
