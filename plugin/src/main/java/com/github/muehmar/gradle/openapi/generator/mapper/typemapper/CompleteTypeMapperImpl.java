package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;

class CompleteTypeMapperImpl implements CompleteTypeMapper {
  private final TypeMapper delegate;

  private CompleteTypeMapperImpl(TypeMapper delegate) {
    this.delegate = delegate;
  }

  public static CompleteTypeMapper ofChain(TypeMapper delegate) {
    return new CompleteTypeMapperImpl(delegate);
  }

  @Override
  public TypeMapResult map(PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
    return delegate
        .map(pojoName, pojoMemberName, schema, this)
        .<IllegalArgumentException>orElseThrow(
            () -> {
              throw new IllegalArgumentException(
                  "Not supported schema for pojo "
                      + pojoName.asString()
                      + " and pojo member "
                      + pojoMemberName.asString()
                      + ": "
                      + schema.toString());
            });
  }
}
