package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class ArraySchemaMapperTest {

  public static final ArraySchemaMapper arraySchemaMapper = new ArraySchemaMapper(null);

  @Test
  void mapSchema_when_arraySchemaWithDateTimeItems_then_correctJavaType() {
    final ArraySchema arraySchema = new ArraySchema().items(new DateTimeSchema());
    final MappedSchema<JavaType> mappedSchema =
        arraySchemaMapper.mapSchema(
            "pojoKey", "key", arraySchema, null, new DateTimeSchemaMapper(null));
    assertEquals(JavaType.javaList(JavaTypes.LOCAL_DATE_TIME), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).minItems(10);
    final MappedSchema<JavaType> mappedSchema =
        arraySchemaMapper.mapSchema(
            "pojoKey", "key", arraySchema, null, new DateTimeSchemaMapper(null));

    assertEquals(
        JavaType.javaList(JavaTypes.LOCAL_DATE_TIME)
            .withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_maxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).maxItems(50);
    final MappedSchema<JavaType> mappedSchema =
        arraySchemaMapper.mapSchema(
            "pojoKey", "key", arraySchema, null, new DateTimeSchemaMapper(null));

    assertEquals(
        JavaType.javaList(JavaTypes.LOCAL_DATE_TIME)
            .withConstraints(Constraints.ofSize(Size.ofMax(50))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minAndMaxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new ArraySchema().items(new DateTimeSchema()).minItems(10).maxItems(50);
    final MappedSchema<JavaType> mappedSchema =
        arraySchemaMapper.mapSchema(
            "pojoKey", "key", arraySchema, null, new DateTimeSchemaMapper(null));

    assertEquals(
        JavaType.javaList(JavaTypes.LOCAL_DATE_TIME)
            .withConstraints(Constraints.ofSize(Size.of(10, 50))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
