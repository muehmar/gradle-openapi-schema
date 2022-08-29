package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ObjectSchema;

public class ObjectSchemaMapper extends BaseTypeMapper<ObjectSchema> {
  ObjectSchemaMapper() {
    super(ObjectSchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName, Name pojoMemberName, ObjectSchema schema, TypeMapper completeMapper) {
    final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
    final ObjectType objectType = ObjectType.ofName(openApiPojoName);
    final OpenApiPojo openApiPojo = new OpenApiPojo(openApiPojoName, schema);
    return TypeMapResult.ofTypeAndOpenApiPojo(objectType, openApiPojo);
  }
}
