package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class MapSchemaMapper extends BaseMemberSchemaMapper<MapSchema> {
  MapSchemaMapper() {
    super(MapSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      MapSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (additionalProperties instanceof Schema) {
      final Schema<?> additionalPropertiesSchema = (Schema<?>) additionalProperties;
      final String $ref = additionalPropertiesSchema.get$ref();

      if ($ref != null) {
        final PojoName mapValuePojoName = ReferenceMapper.getPojoName($ref, pojoName.getSuffix());
        final ObjectType mapValueType = ObjectType.ofName(mapValuePojoName);
        final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), mapValueType);
        return MemberSchemaMapResult.ofType(mapType);
      } else if (additionalPropertiesSchema instanceof ObjectSchema) {
        final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
        final ObjectType objectType = ObjectType.ofName(openApiPojoName);
        final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), objectType);
        final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, additionalPropertiesSchema);

        return MemberSchemaMapResult.ofTypeAndOpenApiPojo(mapType, pojoSchema);
      } else {
        return completeMapper
            .map(pojoName, pojoMemberName, additionalPropertiesSchema)
            .mapType(type -> MapType.ofKeyAndValueType(StringType.noFormat(), type));
      }
    } else {
      throw new IllegalArgumentException(
          "Not supported additionalProperties of class " + additionalProperties.getClass());
    }
  }
}
