package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class NumberSchemaMapperTest {
  private static final NumberSchemaMapper numberSchemaMapper = new NumberSchemaMapper(null);

  @Test
  void mapSchema_when_int32Format_then_integerTypeReturned() {
    final Schema<?> schema = new NumberSchema().format("float");
    final JavaType javaType = numberSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.FLOAT, javaType);
  }

  @Test
  void mapSchema_when_int64Format_then_longTypeReturned() {
    final Schema<?> schema = new NumberSchema().format("double");
    final JavaType javaType = numberSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.DOUBLE, javaType);
  }
}
