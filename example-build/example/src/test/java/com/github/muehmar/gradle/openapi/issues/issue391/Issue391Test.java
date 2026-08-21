package com.github.muehmar.gradle.openapi.issues.issue391;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * In OpenAPI 3.1 the parser keeps native YAML types, so a {@code type: string} enum with non-string
 * literals (e.g. {@code enum: [1, 2]}) previously crashed generation with a {@code
 * ClassCastException}. The literals must be coerced to strings, matching the 3.0 behaviour.
 */
public class Issue391Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void getValue_when_nonStringEnumLiteral_then_coercedToStringValue() {
    assertEquals("1", NumberValuesDto._1.getValue());
    assertEquals("2", NumberValuesDto._2.getValue());
  }

  @Test
  void serialize_when_enumMember_then_correctJson() throws Exception {
    final HolderDto dto = HolderDto.fullBuilder().setNumberValue(NumberValuesDto._1).build();

    assertEquals("{\"numberValue\":\"1\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_enumMember_then_correctDto() throws Exception {
    final HolderDto dto = MAPPER.readValue("{\"numberValue\":\"2\"}", HolderDto.class);

    assertEquals(NumberValuesDto._2, dto.getNumberValueOpt().orElse(null));
  }
}
