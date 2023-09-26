package com.github.muehmar.gradle.openapi.generator.model.schema;

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
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NumberSchemaTest {
  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapToMemberType_when_int32Format_then_integerTypeReturned(String format) {
    final Schema<?> schema = new io.swagger.v3.oas.models.media.NumberSchema().format(format);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    assertEquals(fromFormat(format), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapToMemberType_when_minConstraint_then_typeWithCorrectMinConstraint(String format) {
    final Schema<?> schema =
        new io.swagger.v3.oas.models.media.NumberSchema()
            .format(format)
            .minimum(new BigDecimal(18));
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofDecimalMin(new DecimalMin("18", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapToMemberType_when_maxConstraint_then_typeWithCorrectMaxConstraint(String format) {
    final Schema<?> schema =
        new io.swagger.v3.oas.models.media.NumberSchema()
            .format(format)
            .maximum(new BigDecimal(50));
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format).withConstraints(Constraints.ofDecimalMax(new DecimalMax("50", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapToMemberType_when_minAndMaxConstraint_then_typeWithCorrectMinAndMaxConstraint(
      String format) {
    final Schema<?> schema =
        new io.swagger.v3.oas.models.media.NumberSchema()
            .format(format)
            .minimum(new BigDecimal(18))
            .maximum(new BigDecimal(50));
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format)
            .withConstraints(
                Constraints.ofDecimalMinAndMax(
                    new DecimalMin("18", true), new DecimalMax("50", true)));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(strings = {"float", "double"})
  void mapToMemberType_when_multipleOfConstraint_then_typeWithCorrectMultipleOfConstraint(
      String format) {
    final Schema<?> schema =
        new io.swagger.v3.oas.models.media.NumberSchema()
            .format(format)
            .multipleOf(new BigDecimal(18));
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    final Type expectedType =
        fromFormat(format)
            .withConstraints(Constraints.ofMultipleOf(new MultipleOf(new BigDecimal(18))));

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_noFormat_then_correctDefaultFormat() {
    final Schema<?> schema = new NumberSchema();
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);

    final Type expectedType = NumericType.formatFloat();

    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_numberSchema_then_memberReference() {
    final Schema<?> schema = new NumberSchema();

    final PojoSchema pojoSchema = new PojoSchema(componentName("Number", "Dto"), schema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference memberReference =
        unresolvedMapResult.getPojoMemberReferences().apply(0);
    assertEquals(
        new PojoMemberReference(pojoSchema.getPojoName(), "", NumericType.formatFloat()),
        memberReference);
  }

  private static NumericType fromFormat(String format) {
    return NumericType.ofFormat(
        NumericType.Format.parseString(format)
            .orElseThrow(() -> new IllegalStateException("Invalid format")));
  }
}
