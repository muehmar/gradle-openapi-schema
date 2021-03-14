package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;

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
      final Map<String, Schema> properties = additionalPropertiesSchema.getProperties();

      if ($ref != null) {
        final JavaType valueType = ReferenceMapper.getRefType(pojoSettings, $ref);
        return JavaType.javaMap(JavaTypes.STRING, valueType);
      } else if (properties != null) {
        final String name = JavaResolver.toPascalCase(pojoKey + JavaResolver.toPascalCase(key));
        return JavaType.javaMap(JavaTypes.STRING, JavaType.ofName(name + pojoSettings.getSuffix()))
            .withOpenApiPojo(new OpenApiPojo(name, additionalPropertiesSchema));
      } else {
        throw new IllegalArgumentException(
            "Not supported schema for the map with key " + key + ": " + additionalPropertiesSchema);
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
