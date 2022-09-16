package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import io.swagger.v3.oas.models.media.ArraySchema;
import java.util.Optional;

public class ArrayPojoSchemaMapper implements SinglePojoSchemaMapper {
  private static final CompleteMemberSchemaMapper COMPLETE_TYPE_MAPPER =
      CompleteMemberSchemaMapperFactory.create();

  @Override
  public Optional<PojoSchemaMapResult> map(
      PojoSchema pojoSchema, CompletePojoSchemaMapper completePojoSchemaMapper) {
    if (pojoSchema.getSchema() instanceof ArraySchema) {
      final PojoSchemaMapResult pojoSchemaMapResult =
          fromArraysSchema(
              pojoSchema.getPojoName(),
              (ArraySchema) pojoSchema.getSchema(),
              completePojoSchemaMapper);
      return Optional.of(pojoSchemaMapResult);
    } else {
      return Optional.empty();
    }
  }

  private PojoSchemaMapResult fromArraysSchema(
      PojoName pojoName, ArraySchema schema, CompletePojoSchemaMapper completePojoSchemaMapper) {
    final Constraints constraints = ConstraintsMapper.getMinAndMaxItems(schema);

    final MemberSchemaMapResult memberSchemaMapResult =
        COMPLETE_TYPE_MAPPER.map(pojoName, Name.ofString("value"), schema.getItems());

    final ArrayPojo pojo =
        ArrayPojo.of(
            pojoName, schema.getDescription(), memberSchemaMapResult.getType(), constraints);

    return completePojoSchemaMapper
        .process(memberSchemaMapResult.getPojoSchemas())
        .addPojo(pojo)
        .addSpecifications(memberSchemaMapResult.getRemoteSpecs());
  }
}
