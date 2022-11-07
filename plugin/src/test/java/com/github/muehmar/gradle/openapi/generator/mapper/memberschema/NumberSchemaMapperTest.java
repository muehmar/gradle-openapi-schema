package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
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
    final MemberSchemaMapResult mappedSchema = run(schema);

    assertEquals(fromFormat(format), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new NumberSchema().format(format).minimum(new BigDecimal(18));
    final MemberSchemaMapResult mappedSchema = run(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofDecimalMin(new DecimalMin("18", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new NumberSchema().format(format).maximum(new BigDecimal(50));
    final MemberSchemaMapResult mappedSchema = run(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofDecimalMax(new DecimalMax("50", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapThrowing_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(String format) {
    final Schema<?> schema =
        new NumberSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final MemberSchemaMapResult mappedSchema = run(schema);

    final Type expectedType =
        fromFormat(format)
            .withConstraints(
                Constraints.ofDecimalMinAndMax(
                    new DecimalMin("18", true), new DecimalMax("50", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapThrowing_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new NumberSchema();
    final MemberSchemaMapResult mappedSchema = run(schema);

    final Type expectedType = NumericType.formatFloat();

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  private static NumericType fromFormat(String format) {
    return NumericType.ofFormat(
        NumericType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")));
  }
}
