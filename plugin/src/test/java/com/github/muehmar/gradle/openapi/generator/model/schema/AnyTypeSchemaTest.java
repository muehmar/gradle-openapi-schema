package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class AnyTypeSchemaTest {

  @Test
  void mapToMemberType_when_schemaHasNoTypeAndFormat_then_notTypeReturned() {
    final Schema<Object> schema = new Schema<>();

    final MemberSchemaMapResult result = mapToMemberType(schema);
    assertEquals(AnyType.create(), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_noTypeSchema_then_memberReference() {
    final Schema<?> schema = new Schema<>();

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("AnyType", "Dto"), schema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference memberReference =
        unresolvedMapResult.getPojoMemberReferences().apply(0);
    assertEquals(
        new PojoMemberReference(pojoSchema.getPojoName(), "", AnyType.create()), memberReference);
  }
}
