package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class PropertyScopeMapper {
  private PropertyScopeMapper() {}

  public static PropertyScope mapScope(Schema<?> schema) {
    final boolean writeOnly = Optional.ofNullable(schema.getWriteOnly()).orElse(false);
    final boolean readOnly = Optional.ofNullable(schema.getReadOnly()).orElse(false);
    if (writeOnly && not(readOnly)) {
      return PropertyScope.WRITE_ONLY;
    } else if (readOnly && not(writeOnly)) {
      return PropertyScope.READ_ONLY;
    } else {
      return PropertyScope.DEFAULT;
    }
  }
}
