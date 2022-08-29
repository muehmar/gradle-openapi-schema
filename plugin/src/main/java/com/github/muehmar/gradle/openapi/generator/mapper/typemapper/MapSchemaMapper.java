package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class MapSchemaMapper extends BaseTypeMapper<MapSchema> {
  MapSchemaMapper() {
    super(MapSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, MapSchema schema, TypeMapper completeMapper) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (additionalProperties instanceof Schema) {
      final Schema<?> additionalPropertiesSchema = (Schema<?>) additionalProperties;
      final String $ref = additionalPropertiesSchema.get$ref();

      if ($ref != null) {
        final PojoName mapValuePojoName = ReferenceMapper.getPojoName($ref, pojoName.getSuffix());
        final ObjectType mapValueType = ObjectType.ofName(mapValuePojoName);
        final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), mapValueType);
        return TypeMapResult.ofType(mapType);
      } else if (additionalPropertiesSchema instanceof ObjectSchema) {
        final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
        final ObjectType objectType = ObjectType.ofName(openApiPojoName);
        final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), objectType);
        final OpenApiPojo openApiPojo =
            new OpenApiPojo(openApiPojoName, additionalPropertiesSchema);

        return TypeMapResult.ofTypeAndOpenApiPojo(mapType, openApiPojo);
      } else {
        return completeMapper
            .mapThrowing(pojoName, pojoMemberName, additionalPropertiesSchema, completeMapper)
            .mapType(type -> MapType.ofKeyAndValueType(StringType.noFormat(), type));
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
