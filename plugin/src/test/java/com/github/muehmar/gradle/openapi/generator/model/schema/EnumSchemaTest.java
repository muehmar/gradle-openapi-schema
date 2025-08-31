package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumTypeBuilder;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EnumSchemaTest {

  @Test
  void mapToMemberType_when_enumStringSchema_then_correctType() {
    final StringSchema enumSchema = new StringSchema();
    enumSchema.setEnum(Arrays.asList("male", "female", "divers", "other"));
    enumSchema.setDescription("Test description");

    final MemberSchemaMapResult result = mapToMemberType(enumSchema);

    assertEquals(
        EnumType.ofNameAndMembers(
            Name.ofString("PojoMemberNameEnum"), PList.fromIter(enumSchema.getEnum())),
        result.getType());
    assertEquals(Nullability.NOT_NULLABLE, result.getType().getNullability());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_enumStringSchemaWithFormat_then_correctType() {
    final StringSchema enumSchema = new StringSchema();
    enumSchema.setEnum(Arrays.asList("male", "female", "divers", "other"));
    enumSchema.setDescription("Test description");
    enumSchema.setFormat("Gender");

    final MemberSchemaMapResult result = mapToMemberType(enumSchema);

    final EnumType expectedEnumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("PojoMemberNameEnum"))
            .members(PList.fromIter(enumSchema.getEnum()))
            .nullability(Nullability.NOT_NULLABLE)
            .legacyNullability(Nullability.NOT_NULLABLE)
            .format("Gender")
            .build();
    assertEquals(expectedEnumType, result.getType());
    assertEquals(Nullability.NOT_NULLABLE, result.getType().getNullability());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_enumStringSchema_then_correctPojoMapped() {
    final StringSchema enumSchema = new StringSchema();
    enumSchema.setEnum(Arrays.asList("male", "female", "divers", "other"));
    enumSchema.setDescription("Test description");

    final PojoSchema pojoSchema = new PojoSchema(componentName("Gender", "Dto"), wrap(enumSchema));

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final EnumPojo expectedPojo =
        EnumPojo.of(pojoSchema.getName(), "Test description", PList.fromIter(enumSchema.getEnum()));
    assertEquals(expectedPojo, unresolvedMapResult.getPojos().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
