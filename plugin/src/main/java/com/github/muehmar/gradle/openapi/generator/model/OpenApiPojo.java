package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Container holding a key and the corresponding {@link Schema}. */
@EqualsAndHashCode
@ToString
public class OpenApiPojo {
  private final PojoName name;
  private final Schema<?> schema;

  public OpenApiPojo(Name name, Schema<?> schema) {
    this.name = PojoName.ofName(name.startUpperCase());
    this.schema = schema;
  }

  public OpenApiPojo(PojoName name, Schema<?> schema) {
    this.name = name;
    this.schema = schema;
  }

  public Name getName() {
    return name.getName();
  }

  public PojoName getPojoName() {
    return name;
  }

  public String getSuffix() {
    return name.getSuffix();
  }

  @SuppressWarnings("java:S1452")
  public Schema<?> getSchema() {
    return schema;
  }
}
