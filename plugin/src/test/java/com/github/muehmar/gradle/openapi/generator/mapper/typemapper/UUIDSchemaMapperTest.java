package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.junit.jupiter.api.Test;

class UUIDSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_uuidSchema_then_correctType() {
    final Schema<?> schema = new UUIDSchema();

    final TypeMapResult result = run(schema);

    assertEquals(StringType.ofFormat(UUID), result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }
}
