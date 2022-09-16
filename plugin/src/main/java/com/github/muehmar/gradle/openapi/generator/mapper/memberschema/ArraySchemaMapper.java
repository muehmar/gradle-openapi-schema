package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class ArraySchemaMapper extends BaseMemberSchemaMapper<ArraySchema> {
  ArraySchemaMapper() {
    super(ArraySchema.class);
  }

  @Override
  MemberSchemaMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      ArraySchema schema,
      CompleteMemberSchemaMapper completeMapper) {
    final Schema<?> items = schema.getItems();

    final Constraints constraints = ConstraintsMapper.getMinAndMaxItems(schema);

    if (items instanceof ObjectSchema || items instanceof ComposedSchema) {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
      final ObjectType itemType = ObjectType.ofName(openApiPojoName);
      final ArrayType arrayType = ArrayType.ofItemType(itemType).withConstraints(constraints);
      final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, items);
      return MemberSchemaMapResult.ofTypeAndOpenApiPojo(arrayType, pojoSchema);
    } else {
      return completeMapper
          .map(pojoName, pojoMemberName, items)
          .mapType(itemType -> ArrayType.ofItemType(itemType).withConstraints(constraints));
    }
  }
}
