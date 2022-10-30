package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
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
    final MemberSchemaMapResult result = run(schema);

    assertEquals(fromFormat(format), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).minimum(new BigDecimal(18));
    final MemberSchemaMapResult result = run(schema);

    final Type expectedType = fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).maximum(new BigDecimal(18));
    final MemberSchemaMapResult result = run(schema);

    final Type expectedType = fromFormat(format).withConstraints(Constraints.ofMax(new Max(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapThrowing_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(String format) {
    final Schema<?> schema =
        new IntegerSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final MemberSchemaMapResult result = run(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapThrowing_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new IntegerSchema();
    final MemberSchemaMapResult result = run(schema);

    final Type expectedType = IntegerType.formatInteger();

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  private static IntegerType fromFormat(String format) {
    return IntegerType.ofFormat(
        IntegerType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")));
  }
}
