package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.junit.jupiter.api.Test;

class UUIDSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_uuidSchema_then_correctType() {
    final Schema<?> schema = new UUIDSchema();

    final MemberSchemaMapResult result = run(schema);

    assertEquals(StringType.ofFormat(UUID), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
