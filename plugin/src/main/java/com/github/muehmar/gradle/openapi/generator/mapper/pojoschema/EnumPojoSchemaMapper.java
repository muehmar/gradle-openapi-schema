package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Objects;
import java.util.Optional;

public class EnumPojoSchemaMapper implements SinglePojoSchemaMapper {
  @Override
  public Optional<PojoSchemaMapResult> map(
      PojoSchema pojoSchema, CompletePojoSchemaMapper completePojoSchemaMapper) {
    final Schema<?> schema = pojoSchema.getSchema();
    if (schema instanceof StringSchema && Objects.nonNull(schema.getEnum())) {
      final StringSchema stringSchema = (StringSchema) schema;
      final EnumPojo enumPojo =
          EnumPojo.of(
              pojoSchema.getPojoName(),
              schema.getDescription(),
              PList.fromIter(stringSchema.getEnum()));
      return Optional.ofNullable(PojoSchemaMapResult.ofPojo(enumPojo));
    } else {
      return Optional.empty();
    }
  }
}
