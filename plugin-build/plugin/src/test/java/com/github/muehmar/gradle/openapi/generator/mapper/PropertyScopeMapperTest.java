package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class PropertyScopeMapperTest {
  @Test
  void mapScope_when_noFlag_then_defaultScope() {
    final Schema<Object> schema = new Schema<>();

    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema);

    assertEquals(PropertyScope.DEFAULT, propertyScope);
  }

  @Test
  void mapScope_when_readOnlyAndWriteOnlyFlags_then_defaultScope() {
    final Schema<Object> schema = new Schema<>();
    schema.setWriteOnly(true);
    schema.setReadOnly(true);

    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema);

    assertEquals(PropertyScope.DEFAULT, propertyScope);
  }

  @Test
  void mapScope_when_writeOnlyFlag_then_writeOnlyScope() {
    final Schema<Object> schema = new Schema<>();
    schema.setWriteOnly(true);

    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema);

    assertEquals(PropertyScope.WRITE_ONLY, propertyScope);
  }

  @Test
  void mapScope_when_readOnlyFlag_then_readOnlyScope() {
    final Schema<Object> schema = new Schema<>();
    schema.setReadOnly(true);

    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema);

    assertEquals(PropertyScope.READ_ONLY, propertyScope);
  }
}
