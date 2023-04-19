package com.github.muehmar.gradle.openapi.generator.model.schema;

import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashSet;
import java.util.Set;

public enum SchemaType {
  ARRAY("array"),
  STRING("string"),
  OBJECT("object"),
  BOOLEAN("boolean"),
  INTEGER("integer"),
  NUMBER("number"),
  NULL("null");

  private final String text;

  SchemaType(String text) {
    this.text = text;
  }

  public boolean matchesType(Schema<?> schema) {
    if (SpecVersion.V30.equals(schema.getSpecVersion()) && isType(schema.getType())) {
      return true;
    } else {
      return SpecVersion.V31.equals(schema.getSpecVersion()) && isSingleType(schema.getTypes());
    }
  }

  public boolean matchesTypeAndFormat(Schema<?> schema, String format) {
    return format.equals(schema.getFormat()) && matchesType(schema);
  }

  public boolean isType(String type) {
    return asString().equals(type);
  }

  /**
   * Returns true in case the set contains the type and optionally {@link SchemaType#NULL} but no
   * other type.
   */
  public boolean isSingleType(Set<String> types) {
    final HashSet<String> objects = new HashSet<>();
    objects.add(asString());

    if (objects.equals(types)) {
      return true;
    }

    objects.add(NULL.asString());

    return objects.equals(types);
  }

  public String asString() {
    return text;
  }
}
