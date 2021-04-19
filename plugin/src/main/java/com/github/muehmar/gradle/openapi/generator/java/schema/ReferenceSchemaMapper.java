package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

public class ReferenceSchemaMapper implements JavaSchemaMapper {
  private final JavaSchemaMapper nextMapper;

  public ReferenceSchemaMapper(JavaSchemaMapper nextMapper) {
    this.nextMapper = nextMapper;
  }

  @Override
  public MappedSchema<JavaType> mapSchema(
      Name pojoName,
      Name pojoMemberName,
      Schema<?> schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    if (schema.getType() == null && schema.getFormat() == null && schema.get$ref() != null) {
      final JavaType refType = ReferenceMapper.getRefType(pojoSettings, schema.get$ref());
      return MappedSchema.ofType(refType);
    }

    return nextMapper.mapSchema(pojoName, pojoMemberName, schema, pojoSettings, chain);
  }
}
