package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class ReferenceSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_schemaWithReference_then_correctType() {
    final Schema<?> schema = new Schema<>();
    schema.$ref("#/components/schemas/Person");

    final MemberSchemaMapResult result = run(schema);

    final ObjectType expectedType = ObjectType.ofName(PojoName.ofName(Name.ofString("Person")));
    assertEquals(expectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapSchema_when_schemaWithRemoteReference_then_correctType() {
    final Schema<?> refSchema = new Schema<>().$ref("../components.yml#/components/schemas/Street");

    final MemberSchemaMapResult result = run(refSchema);

    final ObjectType expectedType = ObjectType.ofName(PojoName.ofName(Name.ofString("Street")));
    assertEquals(expectedType, result.getType());
    assertEquals(
        UnmappedItems.ofSpec(OpenApiSpec.fromString("../components.yml")),
        result.getUnmappedItems());
  }
}
