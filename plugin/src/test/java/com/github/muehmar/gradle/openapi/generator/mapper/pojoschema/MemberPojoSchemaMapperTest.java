package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MemberPojoSchemaMapperTest {
  private static final MemberPojoSchemaMapper MEMBER_POJO_SCHEMA_MAPPER =
      new MemberPojoSchemaMapper();

  @Test
  void map_when_stringSchema_then_correctPojoMemberReferenceMapped() {
    final StringSchema stringSchema = new StringSchema();
    stringSchema.setDescription("Test description");

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Text", "Dto"), stringSchema);

    // method call
    final Optional<MapContext> result = MEMBER_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference expectedPojoMemberReference =
        new PojoMemberReference(
            pojoSchema.getPojoName(), stringSchema.getDescription(), StringType.noFormat());
    assertEquals(
        expectedPojoMemberReference, unresolvedMapResult.getPojoMemberReferences().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
