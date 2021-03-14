package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchemaMapperChainTest {

  private static final JavaSchemaMapper CHAIN = SchemaMapperChainFactory.createChain();

  @Test
  void mapSchema_when_dateSchema_then_localDateReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new DateSchema(), null, CHAIN);
    Assertions.assertEquals(JavaTypes.LOCAL_DATE, javaType);
  }

  @Test
  void mapSchema_when_dateTimeSchema_then_localDateTimeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new DateTimeSchema(), null, CHAIN);
    assertEquals(JavaTypes.LOCAL_DATE_TIME, javaType);
  }

  @Test
  void mapSchema_when_booleanSchema_then_booleanTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new BooleanSchema(), null, CHAIN);
    assertEquals(JavaTypes.BOOLEAN, javaType);
  }

  @Test
  void mapSchema_when_integerSchema_then_integerTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new IntegerSchema(), null, CHAIN);
    assertEquals(JavaTypes.INTEGER, javaType);
  }

  @Test
  void mapSchema_when_numberSchema_then_floatTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new NumberSchema(), null, CHAIN);
    assertEquals(JavaTypes.FLOAT, javaType);
  }

  @Test
  void mapSchema_when_arraySchema_then_correctListReturned() {
    final JavaType javaType =
        CHAIN.mapSchema(
            "pojoKey", "key", new ArraySchema().items(new IntegerSchema()), null, CHAIN);
    assertEquals(JavaType.javaList(JavaTypes.INTEGER), javaType);
  }

  @Test
  void mapSchema_when_uuidSchema_then_uuidTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new UUIDSchema(), null, CHAIN);
    assertEquals(JavaTypes.UUID, javaType);
  }

  @Test
  void mapSchema_when_stringSchema_then_correctStringTypeReturned() {
    final JavaType javaType =
        CHAIN.mapSchema("pojoKey", "key", new StringSchema().format("url"), null, CHAIN);
    assertEquals(JavaTypes.URL, javaType);
  }

  @Test
  void mapSchema_when_passwordSchema_then_stringTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new PasswordSchema(), null, CHAIN);
    assertEquals(JavaTypes.STRING, javaType);
  }

  @Test
  void mapSchema_when_binarySchema_then_byteArrayTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new BinarySchema(), null, CHAIN);
    assertEquals(JavaTypes.BYTE_ARRAY, javaType);
  }

  @Test
  void mapSchema_when_fileSchema_then_stringTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new FileSchema(), null, CHAIN);
    assertEquals(JavaTypes.STRING, javaType);
  }

  @Test
  void mapSchema_when_emailSchema_then_stringTypeReturned() {
    final JavaType javaType = CHAIN.mapSchema("pojoKey", "key", new EmailSchema(), null, CHAIN);
    assertEquals(JavaTypes.STRING, javaType);
  }
}
