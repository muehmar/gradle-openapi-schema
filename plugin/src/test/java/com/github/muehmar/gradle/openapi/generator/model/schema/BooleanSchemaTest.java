package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import io.swagger.v3.oas.models.media.BooleanSchema;
import org.junit.jupiter.api.Test;

class BooleanSchemaTest {
  @Test
  void mapToMemberType_when_binarySchema_then_correctType() {
    final BooleanSchema schema = new BooleanSchema();

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(BooleanType.create(NOT_NULLABLE), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_nullableFlagIsTrue_then_typeIsNullable() {
    final BooleanSchema schema = new BooleanSchema();
    schema.setNullable(true);

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(NULLABLE, result.getType().getNullability());
  }

  @Test
  void mapToPojo_when_binarySchema_then_memberReference() {
    final BooleanSchema schema = new BooleanSchema();

    final PojoSchema pojoSchema = new PojoSchema(componentName("Boolean", "Dto"), wrap(schema));

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference memberReference =
        unresolvedMapResult.getPojoMemberReferences().apply(0);
    assertEquals(
        new PojoMemberReference(pojoSchema.getPojoName(), "", BooleanType.create(NOT_NULLABLE)),
        memberReference);
  }
}
