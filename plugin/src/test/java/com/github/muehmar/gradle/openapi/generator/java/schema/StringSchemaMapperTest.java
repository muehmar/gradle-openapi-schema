package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.MappedSchema;
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
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.URL, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.URI, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.LOCAL_TIME, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
