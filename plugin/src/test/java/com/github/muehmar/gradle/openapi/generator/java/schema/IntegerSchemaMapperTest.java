package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerSchemaMapperTest {
  private static final IntegerSchemaMapper INTEGER_SCHEMA_MAPPER = new IntegerSchemaMapper(null);

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapSchema_when_int32Format_then_integerTypeReturned(String format) {
    final IntegerSchema schema = new IntegerSchema().format(format);
    final MappedSchema<JavaType> mappedSchema =
        INTEGER_SCHEMA_MAPPER.mapSchema("pojoKey", "key", schema, null, null);

    assertEquals(fromFormat(format), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapSchema_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).minimum(new BigDecimal(18));
    final MappedSchema<JavaType> mappedSchema =
        INTEGER_SCHEMA_MAPPER.mapSchema("pojoKey", "key", schema, null, null);

    final JavaType expectedType =
        fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapSchema_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).maximum(new BigDecimal(18));
    final MappedSchema<JavaType> mappedSchema =
        INTEGER_SCHEMA_MAPPER.mapSchema("pojoKey", "key", schema, null, null);

    final JavaType expectedType =
        fromFormat(format).withConstraints(Constraints.ofMax(new Max(18)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapSchema_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(String format) {
    final Schema<?> schema =
        new IntegerSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final MappedSchema<JavaType> mappedSchema =
        INTEGER_SCHEMA_MAPPER.mapSchema("pojoKey", "key", schema, null, null);

    final JavaType expectedType =
        fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  private static JavaType fromFormat(String format) {
    if (format.equals("int64")) {
      return JavaTypes.LONG;
    } else {
      return JavaTypes.INTEGER;
    }
  }
}
