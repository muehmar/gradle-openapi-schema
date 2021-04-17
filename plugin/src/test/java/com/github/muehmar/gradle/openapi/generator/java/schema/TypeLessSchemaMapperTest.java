package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class TypeLessSchemaMapperTest {
  @Test
  void mapSchema_when_schemaHasNoTypeAndFormat_then_javaObjectReturned() {
    final TypeLessSchemaMapper schemaMapper = new TypeLessSchemaMapper(null);
    final Schema<Object> schema = new Schema<>();

    final MappedSchema<JavaType> mappedSchema =
        schemaMapper.mapSchema("pojoKey", "key", schema, null, null);
    assertEquals(JavaTypes.OBJECT, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
