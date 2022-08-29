package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
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
    final TypeMapResult mappedSchema = run(arraySchema);
    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_arraySchemaWithComposedAllOfSchemaItems_then_correctType() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema1"));
    composedSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ReferenceSchema2"));

    final ArraySchema arraySchema = new ArraySchema().items(composedSchema);

    final PojoName pojoName = PojoName.ofName(Name.of("Reports"));
    final Name pojoMemberName = Name.of("Invoice");
    final TypeMapResult mappedSchema = run(pojoName, pojoMemberName, arraySchema);
    final ObjectType itemType =
        ObjectType.ofName(PojoName.deriveOpenApiPojoName(pojoName, pojoMemberName));
    assertEquals(ArrayType.ofItemType(itemType), mappedSchema.getType());
    assertEquals(
        PList.of(new OpenApiPojo(Name.of("ReportsInvoice"), composedSchema)),
        mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).minItems(10);
    final TypeMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_maxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema = new ArraySchema().items(new DateTimeSchema()).maxItems(50);
    final TypeMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.ofMax(50))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minAndMaxItemsConstraint_then_typeWithCorrectConstraint() {
    final Schema<?> arraySchema =
        new ArraySchema().items(new DateTimeSchema()).minItems(10).maxItems(50);
    final TypeMapResult mappedSchema = run(arraySchema);

    final StringType itemType = StringType.ofFormat(StringType.Format.DATE_TIME);
    assertEquals(
        ArrayType.ofItemType(itemType).withConstraints(Constraints.ofSize(Size.of(10, 50))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
