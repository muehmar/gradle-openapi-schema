package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.IntegerSchema;
import org.junit.jupiter.api.Test;

class IntegerSchemaMapperTest {
  private static final IntegerSchemaMapper integerSchemaMapper = new IntegerSchemaMapper(null);

  @Test
  void mapSchema_when_int32Format_then_integerTypeReturned() {
    final IntegerSchema schema = new IntegerSchema().format("int32");
    final JavaType javaType = integerSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.INTEGER, javaType);
  }

  @Test
  void mapSchema_when_int64Format_then_longTypeReturned() {
    final IntegerSchema schema = new IntegerSchema().format("int64");
    final JavaType javaType = integerSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.LONG, javaType);
  }
}
