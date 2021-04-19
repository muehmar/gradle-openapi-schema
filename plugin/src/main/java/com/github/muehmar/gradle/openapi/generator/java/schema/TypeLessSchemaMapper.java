package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

/** Maps schema definitions for which no type and format is used. */
public class TypeLessSchemaMapper implements JavaSchemaMapper {
  private final JavaSchemaMapper nextMapper;

  public TypeLessSchemaMapper(JavaSchemaMapper nextMapper) {
    this.nextMapper = nextMapper;
  }

  @Override
  public MappedSchema<JavaType> mapSchema(
      Name pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    if (schema.getType() == null && schema.getFormat() == null) {
      return MappedSchema.ofType(JavaTypes.OBJECT);
    }
    return nextMapper.mapSchema(pojoName, pojoMemberName, schema, pojoSettings, chain);
  }
}
