package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import org.junit.jupiter.api.Test;

class DateTimeSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapSchema_when_binarySchema_then_correctType() {
    final DateTimeSchema schema = new DateTimeSchema();

    final TypeMapResult result = run(schema);

    assertEquals(StringType.ofFormat(DATE_TIME), result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }
}
