package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.BINARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.BinarySchema;
import org.junit.jupiter.api.Test;

class BinarySchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final BinarySchema binarySchema = new BinarySchema();

    final MemberSchemaMapResult result = run(binarySchema);

    assertEquals(StringType.ofFormat(BINARY), result.getType());
    assertEquals(PList.empty(), result.getPojoSchemas());
  }
}
