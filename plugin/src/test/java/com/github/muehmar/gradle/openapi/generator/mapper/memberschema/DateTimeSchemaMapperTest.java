package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import org.junit.jupiter.api.Test;

class DateTimeSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final DateTimeSchema schema = new DateTimeSchema();

    final MemberSchemaMapResult result = run(schema);

    assertEquals(StringType.ofFormat(DATE_TIME), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }
}
