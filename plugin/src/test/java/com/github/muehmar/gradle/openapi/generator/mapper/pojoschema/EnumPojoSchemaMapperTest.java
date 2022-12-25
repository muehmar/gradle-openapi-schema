package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EnumPojoSchemaMapperTest {
  private static final EnumPojoSchemaMapper ENUM_POJO_SCHEMA_MAPPER = new EnumPojoSchemaMapper();

  @Test
  void map_when_enumStringSchema_then_correctPojoMapped() {
    final StringSchema enumSchema = new StringSchema();
    enumSchema.setEnum(Arrays.asList("male", "female", "divers", "other"));
    enumSchema.setDescription("Test description");

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Gender", "Dto"), enumSchema);

    // method call
    final Optional<MapContext> result = ENUM_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getUnresolvedComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final EnumPojo expectedPojo =
        EnumPojo.of(
            pojoSchema.getPojoName(), "Test description", PList.fromIter(enumSchema.getEnum()));
    assertEquals(expectedPojo, unresolvedMapResult.getPojos().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
