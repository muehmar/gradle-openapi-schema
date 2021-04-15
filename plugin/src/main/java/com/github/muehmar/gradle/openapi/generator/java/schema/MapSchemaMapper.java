package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
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
  MappedSchema<JavaType> mapSpecificSchema(
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
        return MappedSchema.ofType(JavaType.javaMap(JavaTypes.STRING, valueType));
      } else if (additionalPropertiesSchema instanceof ObjectSchema) {
        final String openApiPojoKey = JavaResolver.toPascalCase(pojoKey, key);
        final JavaType addPropertiesType =
            JavaType.ofOpenApiSchema(openApiPojoKey, pojoSettings.getSuffix());
        final JavaType javaType = JavaType.javaMap(JavaTypes.STRING, addPropertiesType);
        final OpenApiPojo openApiPojo = new OpenApiPojo(openApiPojoKey, additionalPropertiesSchema);
        return MappedSchema.ofTypeAndOpenApiPojo(javaType, openApiPojo);
      } else {
        return chain
            .mapSchema(pojoKey, key, additionalPropertiesSchema, pojoSettings, chain)
            .mapType(
                additionalPropertiesType ->
                    JavaType.javaMap(JavaTypes.STRING, additionalPropertiesType));
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
