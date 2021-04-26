package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
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
      Name pojoName,
      Name pojoMemberName,
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
        final Name openApiPojoName = JavaResolver.toPascalCase(pojoName, pojoMemberName);
        final JavaType addPropertiesType =
            JavaType.ofOpenApiSchema(openApiPojoName, pojoSettings.getSuffix());
        final JavaType javaType = JavaType.javaMap(JavaTypes.STRING, addPropertiesType);
        final OpenApiPojo openApiPojo =
            new OpenApiPojo(openApiPojoName, additionalPropertiesSchema);
        return MappedSchema.ofTypeAndOpenApiPojo(javaType, openApiPojo);
      } else {
        return chain
            .mapSchema(pojoName, pojoMemberName, additionalPropertiesSchema, pojoSettings, chain)
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
