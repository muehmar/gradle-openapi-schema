package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoNames;
import io.swagger.v3.oas.models.media.Schema;

abstract class BaseTypeMapperTest {
  private static final CompleteTypeMapper TYPE_MAPPER = CompleteTypeMapperFactory.create();

  protected TypeMapResult run(Schema<?> schema) {
    return TYPE_MAPPER.map(PojoNames.POJO_NAME, Name.of("pojoMemberName"), schema);
  }

  protected TypeMapResult run(PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
    return TYPE_MAPPER.map(pojoName, pojoMemberName, schema);
  }
}
