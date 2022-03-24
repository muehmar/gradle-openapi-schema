package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class TestOptionalNullable {

  @Test
  void deserialize_test() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue("{\"prop1\":\"Hello\"}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(OptionalNullableDto.Tristate.ofAbsent(), dto.getProp2());
  }

  @Test
  void deserialize_test2() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue("{\"prop1\":\"Hello\",\"prop2\":null}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(OptionalNullableDto.Tristate.ofNull(), dto.getProp2());
  }

  @Test
  void deserialize_test3() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue("{\"prop1\":\"Hello\",\"prop2\":\"World!\"}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(OptionalNullableDto.Tristate.ofValue("World!"), dto.getProp2());
  }

  @Test
  void serialize_test1() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", null, false);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\"}", output);
  }

  @Test
  void serialize_test2() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", null, true);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":null}", output);
  }

  @Test
  void serialize_test3() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", "World", true);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":\"World\"}", output);
  }

  @Test
  void serialize_test4() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", "World", false);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":\"World\"}", output);
  }
}
