package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class ReferenceSchemaTest {
  @Test
  void mapToMemberType_when_schemaWithReference_then_correctType() {
    final Schema<?> schema = new Schema<>();
    schema.$ref("#/components/schemas/Person");

    final MemberSchemaMapResult result = mapToMemberType(schema);

    final ObjectType expectedType = ObjectType.ofName(PojoName.ofName(Name.ofString("Person")));
    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_schemaWithRemoteReference_then_correctType() {
    final Schema<?> refSchema = new Schema<>().$ref("../components.yml#/components/schemas/Street");

    final MemberSchemaMapResult result = mapToMemberType(refSchema);

    final ObjectType expectedType = ObjectType.ofName(PojoName.ofName(Name.ofString("Street")));
    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofSpec(OpenApiSpec.fromString("../components.yml")),
        result.getUnmappedItems());
  }
}
