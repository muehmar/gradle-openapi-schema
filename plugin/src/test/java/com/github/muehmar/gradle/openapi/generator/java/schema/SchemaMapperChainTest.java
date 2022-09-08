package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SchemaMapperChainTest {

  private static final JavaSchemaMapper CHAIN = SchemaMapperChainFactory.createChain();
  private static final PojoSettings SETTINGS = TestPojoSettings.defaultSettings();

  @Test
  void mapSchema_when_dateSchema_then_localDateReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new DateSchema(),
            SETTINGS,
            CHAIN);
    Assertions.assertEquals(JavaTypes.LOCAL_DATE, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_dateTimeSchema_then_localDateTimeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new DateTimeSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.LOCAL_DATE_TIME, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_booleanSchema_then_booleanTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new BooleanSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.BOOLEAN, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_integerSchema_then_integerTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new IntegerSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.INTEGER, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_numberSchema_then_floatTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new NumberSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.FLOAT, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_arraySchema_then_correctListReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new ArraySchema().items(new IntegerSchema()),
            SETTINGS,
            CHAIN);
    assertEquals(JavaType.javaList(JavaTypes.INTEGER), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_uuidSchema_then_uuidTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new UUIDSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.UUID, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_stringSchema_then_correctStringTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new StringSchema().format("url"),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.URL, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_passwordSchema_then_stringTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new PasswordSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.STRING, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_binarySchema_then_byteArrayTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new BinarySchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.BYTE_ARRAY, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_fileSchema_then_stringTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new FileSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.STRING, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_emailSchema_then_stringTypeReturned() {
    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            new EmailSchema(),
            SETTINGS,
            CHAIN);
    assertEquals(JavaTypes.STRING.withConstraints(Constraints.ofEmail()), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_objectSchema_then_correctNameAndOpenApiSchema() {
    final ObjectSchema objectSchema = new ObjectSchema();

    final MappedSchema<JavaType> mappedSchema =
        CHAIN.mapSchema(
            Name.ofString("pojoName"),
            Name.ofString("pojoMemberName"),
            objectSchema,
            SETTINGS,
            CHAIN);

    final Name expectedName = Name.ofString("PojoNamePojoMemberName");
    assertEquals(JavaType.ofOpenApiSchema(expectedName, "Dto"), mappedSchema.getType());
    assertEquals(
        PList.single(new OpenApiPojo(expectedName, objectSchema)), mappedSchema.getOpenApiPojos());
  }
}
