package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerSchemaMapperTest extends BaseTypeMapperTest {

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_int32Format_then_integerTypeReturned(String format) {
    final IntegerSchema schema = new IntegerSchema().format(format);
    final TypeMapResult result = run(schema);

    assertEquals(fromFormat(format), result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).minimum(new BigDecimal(18));
    final TypeMapResult result = run(schema);

    final NewType expectedType = fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).maximum(new BigDecimal(18));
    final TypeMapResult result = run(schema);

    final NewType expectedType = fromFormat(format).withConstraints(Constraints.ofMax(new Max(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(String format) {
    final Schema<?> schema =
        new IntegerSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final TypeMapResult result = run(schema);

    final NewType expectedType =
        fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50)));

    assertEquals(expectedType, result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new IntegerSchema();
    final TypeMapResult mappedSchema = run(schema);

    final NewType expectedType = NumericType.formatInteger();

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  private static NumericType fromFormat(String format) {
    return NumericType.ofFormat(
        NumericType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")));
  }
}
