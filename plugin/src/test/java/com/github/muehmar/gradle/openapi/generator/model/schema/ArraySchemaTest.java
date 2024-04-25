package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArraySchemaTest {

  @Test
  void mapToMemberType_when_arraySchemaWithDateTimeItems_then_correctType() {
    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema());
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);
    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(ArrayType.ofItemType(itemType, NOT_NULLABLE), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_nullableFlagIsTrue_then_typeIsNullable() {
    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema());
    arraySchema.setNullable(true);

    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    assertEquals(NULLABLE, mappedSchema.getType().getNullability());
  }

  @Test
  void mapToMemberType_when_objectItemTypeAndNullableFlagIsTrue_then_typeIsNullable() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setName("Person");
    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(objectSchema);
    arraySchema.setNullable(true);

    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    assertEquals(NULLABLE, mappedSchema.getType().getNullability());
  }

  @Test
  void mapToMemberType_when_arraySchemaWithComposedAllOfSchemaItems_then_correctType() {
    final io.swagger.v3.oas.models.media.ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema2"));

    final io.swagger.v3.oas.models.media.ArraySchema arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(composedSchema);

    final ComponentName componentName = componentName("Reports", "Dto");
    final Name pojoMemberName = Name.ofString("Invoice");
    final MemberSchemaMapResult mappedSchema =
        mapToMemberType(componentName, pojoMemberName, arraySchema);
    final ObjectType itemType =
        StandardObjectType.ofName(
            componentName.deriveMemberSchemaName(pojoMemberName).getPojoName());
    assertEquals(ArrayType.ofItemType(itemType, NOT_NULLABLE), mappedSchema.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(
            new PojoSchema(componentName.deriveMemberSchemaName(pojoMemberName), composedSchema)),
        mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_minItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new io.swagger.v3.oas.models.media.ArraySchema().items(new DateTimeSchema()).minItems(10);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.ofMin(10))),
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
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.ofMax(50))),
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
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.of(10, 50))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_uniqueItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).uniqueItems(true);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(true)),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void mapToPojo_when_stringItem_then_mappedToCorrectPojo(boolean nullable) {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setDescription("Test description");
    arraySchema.setNullable(nullable);
    arraySchema.setItems(new io.swagger.v3.oas.models.media.StringSchema());

    final PojoSchema pojoSchema = new PojoSchema(componentName("Array", "Dto"), arraySchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ArrayPojo expectedPojo =
        ArrayPojo.of(
            pojoSchema.getName(),
            "Test description",
            Nullability.fromBoolean(nullable),
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

    final PojoSchema pojoSchema = new PojoSchema(componentName("Array", "Dto"), arraySchema);

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

    final PojoSchema pojoSchema = new PojoSchema(componentName("Array", "Dto"), arraySchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());

    final Pojo pojo = unresolvedMapResult.getPojos().apply(0);
    assertTrue(pojo instanceof ArrayPojo);

    assertEquals(Constraints.ofSize(Size.of(5, 10)), ((ArrayPojo) pojo).getConstraints());
  }
}
