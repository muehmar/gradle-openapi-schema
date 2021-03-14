package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;

public class MapSchemaMapper extends BaseSchemaMapper<MapSchema> {

  public MapSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(MapSchema.class, nextTypeMapper);
  }

  @Override
  JavaType mapSpecificSchema(PojoSettings pojoSettings, MapSchema schema, JavaSchemaMapper chain) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (additionalProperties instanceof Schema) {
      final String $ref = ((Schema<?>) additionalProperties).get$ref();
      if ($ref != null) {
        final JavaType valueType = ReferenceMapper.getRefType(pojoSettings, $ref);
        return JavaType.javaMap(JavaTypes.STRING, valueType);
      } else {
        throw new IllegalArgumentException(
            "Only map schemas with references are supported yet: " + additionalProperties);
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
