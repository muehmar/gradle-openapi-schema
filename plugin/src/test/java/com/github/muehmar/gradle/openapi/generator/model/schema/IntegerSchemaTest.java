package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerSchemaTest {

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_int32Format_then_integerTypeReturned(String format) {
    final IntegerSchema schema = new IntegerSchema().format(format);
    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(fromFormat(format), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_nullableFlagIsTrue_then_typeIsNullable(String format) {
    final IntegerSchema schema = new IntegerSchema().format(format);
    schema.setNullable(true);
    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(NULLABLE, result.getType().getNullability());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).minimum(new BigDecimal(18));
    final MemberSchemaMapResult result = mapToMemberType(schema);

    final Type expectedType = fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema = new IntegerSchema().format(format).maximum(new BigDecimal(18));
    final MemberSchemaMapResult result = mapToMemberType(schema);

    final Type expectedType = fromFormat(format).withConstraints(Constraints.ofMax(new Max(18)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(
      String format) {
    final Schema<?> schema =
        new IntegerSchema().format(format).minimum(new BigDecimal(18)).maximum(new BigDecimal(50));
    final MemberSchemaMapResult result = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofMin(new Min(18)).withMax(new Max(50)));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"int32", "int64"})
  void mapToMemberType_when_multipleOfConstraint_then_typeWithCorrectMultipleOfConstraint(
      String format) {
    final Schema<?> schema = new IntegerSchema().format(format).multipleOf(new BigDecimal(18));
    final MemberSchemaMapResult result = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format)
            .withConstraints(Constraints.ofMultipleOf(new MultipleOf(new BigDecimal(18))));

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new IntegerSchema();
    final MemberSchemaMapResult result = mapToMemberType(schema);

    final Type expectedType = IntegerType.formatInteger();

    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_integerSchema_then_memberReference() {
    final Schema<?> schema = new IntegerSchema();

    final PojoSchema pojoSchema = new PojoSchema(componentName("Integer", "Dto"), schema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference memberReference =
        unresolvedMapResult.getPojoMemberReferences().apply(0);
    assertEquals(
        new PojoMemberReference(pojoSchema.getPojoName(), "", IntegerType.formatInteger()),
        memberReference);
  }

  private static IntegerType fromFormat(String format) {
    return IntegerType.ofFormat(
        IntegerType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")),
        NOT_NULLABLE);
  }
}
