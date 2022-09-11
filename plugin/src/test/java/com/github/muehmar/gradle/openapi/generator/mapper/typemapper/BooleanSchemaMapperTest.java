package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import io.swagger.v3.oas.models.media.BooleanSchema;
import org.junit.jupiter.api.Test;

class BooleanSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final BooleanSchema schema = new BooleanSchema();

    final TypeMapResult result = run(schema);

    assertEquals(BooleanType.create(), result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }
}
