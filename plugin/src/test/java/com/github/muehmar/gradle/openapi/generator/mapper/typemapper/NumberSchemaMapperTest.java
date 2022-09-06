package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NumberSchemaMapperTest extends BaseTypeMapperTest {
  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_int32Format_then_integerTypeReturned(String format) {
    final Schema<?> schema = new NumberSchema().format(format);
    final TypeMapResult mappedSchema = run(schema);

    assertEquals(fromFormat(format), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new NumberSchema().format(format).minimum(new BigDecimal(18));
    final TypeMapResult mappedSchema = run(schema);

    final NewType expectedType = fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new NumberSchema().format(format).maximum(new BigDecimal(50));
    final TypeMapResult mappedSchema = run(schema);

    final NewType expectedType = fromFormat(format).withConstraints(Constraints.ofMax(new Max(50)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(String format) {
    final Schema<?> schema =
        new NumberSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final TypeMapResult mappedSchema = run(schema);

    final NewType expectedType =
        fromFormat(format).withConstraints(Constraints.ofMinAndMax(new Min(18), new Max(50)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new NumberSchema();
    final TypeMapResult mappedSchema = run(schema);

    final NewType expectedType = NumericType.formatFloat();

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  private static NumericType fromFormat(String format) {
    return NumericType.ofFormat(
        NumericType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")));
  }
}
