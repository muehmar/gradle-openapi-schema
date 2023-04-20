package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

class ArraySchemaTest {

  @Test
  void mapToMemberType_when_arraySchemaWithDateTimeItems_then_correctType() {
    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema());
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);
    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_arraySchemaWithComposedAllOfSchemaItems_then_correctType() {
    final io.swagger.v3.oas.models.media.ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema2"));

    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(composedSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix("Reports", "Dto");
    final Name pojoMemberName = Name.ofString("Invoice");
    final MemberSchemaMapResult mappedSchema =
        mapToMemberType(pojoName, pojoMemberName, arraySchema);
    final ObjectType itemType =
        ObjectType.ofName(PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName));
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(
            new PojoSchema(PojoName.ofNameAndSuffix("ReportsInvoice", "Dto"), composedSchema)),
        mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_minItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema()).minItems(10);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_maxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema()).maxItems(50);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMax(50))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_minAndMaxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema()
            .items(new DateTimeSchema())
            .minItems(10)
            .maxItems(50);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.of(10, 50))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_uniqueItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).uniqueItems(true);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofUniqueItems(true)),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_stringItem_then_mappedToCorrectPojo() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setDescription("Test description");
    arraySchema.setItems(new io.swagger.v3.oas.models.media.StringSchema());

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Array", "Dto"), arraySchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ArrayPojo expectedPojo =
        ArrayPojo.of(
            pojoSchema.getPojoName(),
            "Test description",
            StringType.noFormat(),
            Constraints.empty());
    assertEquals(expectedPojo, unresolvedMapResult.getPojos().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_uniqueItemsConstraint_then_pojoWithCorrectConstraint() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setDescription("Test description");
    arraySchema.setItems(new io.swagger.v3.oas.models.media.StringSchema());
    arraySchema.setUniqueItems(true);

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Array", "Dto"), arraySchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());

    final Pojo pojo = unresolvedMapResult.getPojos().apply(0);
    assertTrue(pojo instanceof ArrayPojo);

    assertTrue(((ArrayPojo) pojo).getConstraints().isUniqueItems());
  }

  @Test
  void mapToPojo_when_minMaxItemsConstraint_then_pojoWithCorrectConstraint() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setDescription("Test description");
    arraySchema.setItems(new StringSchema());
    arraySchema.minItems(5);
    arraySchema.maxItems(10);

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Array", "Dto"), arraySchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());

    final Pojo pojo = unresolvedMapResult.getPojos().apply(0);
    assertTrue(pojo instanceof ArrayPojo);

    assertEquals(Constraints.ofSize(Size.of(5, 10)), ((ArrayPojo) pojo).getConstraints());
  }
}
