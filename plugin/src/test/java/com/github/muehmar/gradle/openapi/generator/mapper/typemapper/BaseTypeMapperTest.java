package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoNames;
import io.swagger.v3.oas.models.media.Schema;

abstract class BaseTypeMapperTest {
  private static final TypeMapper TYPE_MAPPER = CompleteTypeMapper.create();

  protected TypeMapResult run(Schema<?> schema) {
    return TYPE_MAPPER.mapThrowing(
        PojoNames.POJO_NAME, Name.of("pojoMemberName"), schema, TYPE_MAPPER);
  }

  protected TypeMapResult run(PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
    return TYPE_MAPPER.mapThrowing(pojoName, pojoMemberName, schema, TYPE_MAPPER);
  }
}
