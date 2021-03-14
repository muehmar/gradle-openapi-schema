package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

class StringSchemaMapperTest {

  private static final StringSchemaMapper stringSchemaMapper = new StringSchemaMapper(null);

  @Test
  void mapSchema_when_urlFormat_then_correctUrlJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("url");
    final JavaType javaType = stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.URL, javaType);
  }

  @Test
  void mapSchema_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final JavaType javaType = stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.URI, javaType);
  }

  @Test
  void mapSchema_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final JavaType javaType = stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.LOCAL_TIME, javaType);
  }
}
