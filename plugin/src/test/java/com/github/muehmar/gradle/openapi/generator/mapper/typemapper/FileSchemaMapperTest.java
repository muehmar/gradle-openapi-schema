package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.BINARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.FileSchema;
import org.junit.jupiter.api.Test;

class FileSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final FileSchema schema = new FileSchema();

    final TypeMapResult result = run(schema);

    assertEquals(StringType.ofFormat(BINARY), result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }
}