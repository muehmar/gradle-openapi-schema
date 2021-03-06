package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

public class ArraySchemaMapper extends BaseSchemaMapper<ArraySchema> {

  public ArraySchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(ArraySchema.class, nextTypeMapper);
  }

  @Override
  JavaType mapSpecificSchema(
      PojoSettings pojoSettings, ArraySchema schema, JavaSchemaMapper chain) {
    final Schema<?> items = schema.getItems();
    final JavaType itemType = chain.mapSchema(pojoSettings, items, chain);
    return JavaType.javaList(itemType);
  }
}
