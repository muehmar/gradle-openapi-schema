package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public interface TypeMapper {
  Optional<TypeMapResult> map(
      PojoName pojoName, Name pojoMemberName, Schema<?> schema, CompleteTypeMapper completeMapper);

  default TypeMapper or(TypeMapper next) {
    final TypeMapper self = this;
    return ((pojoName, pojoMemberName, schema, completeMapper) ->
        Optionals.or(
            () -> self.map(pojoName, pojoMemberName, schema, completeMapper),
            () -> next.map(pojoName, pojoMemberName, schema, completeMapper)));
  }
}
