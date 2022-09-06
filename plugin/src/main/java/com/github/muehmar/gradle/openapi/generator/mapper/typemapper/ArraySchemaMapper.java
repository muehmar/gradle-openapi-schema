package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.schema.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

public class ArraySchemaMapper extends BaseTypeMapper<ArraySchema> {
  ArraySchemaMapper() {
    super(ArraySchema.class);
  }

  @Override
  TypeMapResult mapSpecificSchema(
      PojoName pojoName,
      Name pojoMemberName,
      ArraySchema schema,
      CompleteTypeMapper completeMapper) {
    final Schema<?> items = schema.getItems();

    final Constraints constraints = ConstraintsMapper.getMinAndMaxItems(schema);

    if (items instanceof ObjectSchema || items instanceof ComposedSchema) {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName);
      final ObjectType itemType = ObjectType.ofName(openApiPojoName);
      final ArrayType arrayType = ArrayType.ofItemType(itemType).withConstraints(constraints);
      final OpenApiPojo openApiPojo = new OpenApiPojo(openApiPojoName, items);
      return TypeMapResult.ofTypeAndOpenApiPojo(arrayType, openApiPojo);
    } else {
      return completeMapper
          .map(pojoName, pojoMemberName, items)
          .mapType(itemType -> ArrayType.ofItemType(itemType).withConstraints(constraints));
    }
  }
}
