package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.BINARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.FileSchema;
import org.junit.jupiter.api.Test;

class FileSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final FileSchema schema = new FileSchema();

    final MemberSchemaMapResult result = run(schema);

    assertEquals(StringType.ofFormat(BINARY), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
