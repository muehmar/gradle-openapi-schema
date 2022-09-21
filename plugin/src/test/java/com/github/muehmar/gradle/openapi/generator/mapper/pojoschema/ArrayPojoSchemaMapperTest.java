package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ArrayPojoSchemaMapperTest {
  private static final ArrayPojoSchemaMapper ARRAY_POJO_SCHEMA_MAPPER = new ArrayPojoSchemaMapper();

  @Test
  void map_when_stringItem_then_mappedToCorrectPojo() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setDescription("Test description");
    arraySchema.setItems(new StringSchema());

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Array", "Dto"), arraySchema);

    // method call
    final Optional<MapContext> result = ARRAY_POJO_SCHEMA_MAPPER.map(pojoSchema);

    assertTrue(result.isPresent());
    final MapContext mapContext = result.get();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(1, unresolvedMapResult.getPojos().size());
    assertEquals(0, unresolvedMapResult.getComposedPojos().size());
    assertEquals(0, unresolvedMapResult.getPojoMemberReferences().size());

    final ArrayPojo expectedPojo =
        ArrayPojo.of(
            pojoSchema.getPojoName(),
            "Test description",
            StringType.noFormat(),
            Constraints.empty());
    assertEquals(expectedPojo, unresolvedMapResult.getPojos().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
