package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ObjectSchema;

public class ObjectSchemaMapper extends BaseMemberSchemaMapper<ObjectSchema> {
  ObjectSchemaMapper() {
    super(ObjectSchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      ObjectSchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
    final ObjectType objectType = ObjectType.ofName(openApiPojoName);
    final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, schema);
    return MemberSchemaMapResult.ofTypeAndPojoSchema(objectType, pojoSchema);
  }
}
