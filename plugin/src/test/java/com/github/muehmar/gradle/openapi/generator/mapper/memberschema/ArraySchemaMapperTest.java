package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class ArraySchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapSchema_when_arraySchemaWithDateTimeItems_then_correctType() {
    final ArraySchema arraySchema = new ArraySchema().items(new DateTimeSchema());
    final MemberSchemaMapResult mappedSchema = run(arraySchema);
    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapSchema_when_arraySchemaWithComposedAllOfSchemaItems_then_correctType() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema2"));

    final ArraySchema arraySchema = new ArraySchema().items(composedSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix("Reports", "Dto");
    final Name pojoMemberName = Name.ofString("Invoice");
    final MemberSchemaMapResult mappedSchema = run(pojoName, pojoMemberName, arraySchema);
    final ObjectType itemType =
        ObjectType.ofName(PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName));
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(
        UnmappedItems.ofPojoSchema(
            new PojoSchema(PojoName.ofNameAndSuffix("ReportsInvoice", "Dto"), composedSchema)),
        mappedSchema.getUnmappedItems());
  }

  @Test
  void mapSchema_when_minItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).minItems(10);
    final MemberSchemaMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapSchema_when_maxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).maxItems(50);
    final MemberSchemaMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMax(50))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapSchema_when_minAndMaxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new ArraySchema().items(new DateTimeSchema()).minItems(10).maxItems(50);
    final MemberSchemaMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.of(10, 50))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapSchema_when_uniqueItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).uniqueItems(true);
    final MemberSchemaMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofUniqueItems(true)),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }
}
