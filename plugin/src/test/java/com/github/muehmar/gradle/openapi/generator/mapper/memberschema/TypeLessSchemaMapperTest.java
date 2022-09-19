package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class TypeLessSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_schemaHasNoTypeAndFormat_then_notTypeReturned() {
    final Schema<Object> schema = new Schema<>();

    final MemberSchemaMapResult result = run(schema);
    assertEquals(NoType.create(), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
