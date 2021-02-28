package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import org.junit.jupiter.api.Test;

class ArraySchemaMapperTest {

  public static final ArraySchemaMapper arraySchemaMapper = new ArraySchemaMapper(null);

  @Test
  void mapSchema_when_arraySchemaWithDateTimeItems_then_correctJavaType() {
    final ArraySchema arraySchema = new ArraySchema().items(new DateTimeSchema());
    final JavaType javaType =
        arraySchemaMapper.mapSchema(null, arraySchema, new DateTimeSchemaMapper(null));
    assertEquals(JavaType.javaList(JavaTypes.LOCAL_DATE_TIME), javaType);
  }
}
