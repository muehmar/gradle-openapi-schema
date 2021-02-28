package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

@FunctionalInterface
public interface JavaSchemaMapper {
  JavaType mapSchema(PojoSettings pojoSettings, Schema<?> schema, JavaSchemaMapper chain);
}
