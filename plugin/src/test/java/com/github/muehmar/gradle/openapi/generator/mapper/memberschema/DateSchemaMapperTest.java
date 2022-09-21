package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.DateSchema;
import org.junit.jupiter.api.Test;

class DateSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final DateSchema schema = new DateSchema();

    final MemberSchemaMapResult result = run(schema);

    assertEquals(StringType.ofFormat(DATE), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
