package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class MapSchemaMapper extends BaseSchemaMapper<MapSchema> {

  public MapSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(MapSchema.class, nextTypeMapper);
  }

  @Override
  JavaType mapSpecificSchema(
      String pojoKey,
      String key,
      MapSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (additionalProperties instanceof Schema) {
      final Schema<?> additionalPropertiesSchema = (Schema<?>) additionalProperties;
      final String $ref = additionalPropertiesSchema.get$ref();

      if ($ref != null) {
        final JavaType valueType = ReferenceMapper.getRefType(pojoSettings, $ref);
        return JavaType.javaMap(JavaTypes.STRING, valueType);
      } else if (additionalPropertiesSchema instanceof ObjectSchema) {
        final JavaType addPropertiesType =
            JavaType.ofOpenApiSchema(
                pojoKey, key, pojoSettings.getSuffix(), additionalPropertiesSchema);
        return JavaType.javaMap(JavaTypes.STRING, addPropertiesType);
      } else {
        final JavaType type =
            chain.mapSchema(pojoKey, key, additionalPropertiesSchema, pojoSettings, chain);
        return JavaType.javaMap(JavaTypes.STRING, type);
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
