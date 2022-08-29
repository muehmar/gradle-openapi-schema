package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class TypeLessSchemaMapper implements TypeMapper {
  @Override
  public Optional<TypeMapResult> map(
      PojoName pojoName, Name pojoMemberName, Schema<?> schema, TypeMapper completeMapper) {
    if (schema.getType() == null && schema.getFormat() == null) {
      final NoType noType = NoType.create();
      final TypeMapResult typeMapResult = TypeMapResult.ofType(noType);
      return Optional.of(typeMapResult);
    }
    return Optional.empty();
  }
}
