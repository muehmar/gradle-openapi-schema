package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.Schema;

class CompleteMemberSchemaMapperImpl implements CompleteMemberSchemaMapper {
  private final MemberSchemaMapper delegate;

  private CompleteMemberSchemaMapperImpl(MemberSchemaMapper delegate) {
    this.delegate = delegate;
  }

  public static CompleteMemberSchemaMapper ofChain(MemberSchemaMapper delegate) {
    return new CompleteMemberSchemaMapperImpl(delegate);
  }

  @Override
  public MemberSchemaMapResult map(PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
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
