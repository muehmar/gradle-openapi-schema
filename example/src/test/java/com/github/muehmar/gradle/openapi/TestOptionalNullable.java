package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestOptionalNullable {

  @Test
  void deserialize_when_onlyProp1Present_then_correctProps() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue("{\"prop1\":\"Hello\",\"prop2\":null}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(Optional.empty(), dto.getProp2());
    assertEquals(Optional.empty(), dto.getProp3());
    assertEquals(OptionalNullableDto.Tristate.ofAbsent(), dto.getProp4());
  }

  @Test
  void deserialize_when_prop2NotNull_then_correctProps() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue("{\"prop1\":\"Hello\",\"prop2\":\"World\"}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(Optional.of("World"), dto.getProp2());
    assertEquals(Optional.empty(), dto.getProp3());
    assertEquals(OptionalNullableDto.Tristate.ofAbsent(), dto.getProp4());
  }

  @Test
  void deserialize_when_prop3Present_then_correctProps() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue(
            "{\"prop1\":\"Hello\",\"prop2\":null,\"prop3\":\"World\"}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(Optional.empty(), dto.getProp2());
    assertEquals(Optional.of("World"), dto.getProp3());
    assertEquals(OptionalNullableDto.Tristate.ofAbsent(), dto.getProp4());
  }

  @Test
  void deserialize_when_prop4Null_then_correctProps() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue(
            "{\"prop1\":\"Hello\",\"prop2\":null,\"prop4\":null}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(Optional.empty(), dto.getProp2());
    assertEquals(Optional.empty(), dto.getProp3());
    assertEquals(OptionalNullableDto.Tristate.ofNull(), dto.getProp4());
  }

  @Test
  void deserialize_when_prop4Present_then_correctProps() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        mapper.readValue(
            "{\"prop1\":\"Hello\",\"prop2\":null,\"prop4\":\"World\"}", OptionalNullableDto.class);

    assertEquals("Hello", dto.getProp1());
    assertEquals(Optional.empty(), dto.getProp2());
    assertEquals(Optional.empty(), dto.getProp3());
    assertEquals(OptionalNullableDto.Tristate.ofValue("World"), dto.getProp4());
  }

  @Test
  void serialize_when_onlyProp1Present_then_correctSerialized() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", null, true, null, null, false);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":null}", output);
  }

  @Test
  void serialize_when_prop2Present_then_correctSerialized() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        new OptionalNullableDto("Hello", "World", true, null, null, false);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":\"World\"}", output);
  }

  @Test
  void serialize_when_prop3Present_then_correctSerialized() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        new OptionalNullableDto("Hello", null, true, "World", null, false);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":null,\"prop3\":\"World\"}", output);
  }

  @Test
  void serialize_when_prop4Null_then_correctSerialized() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto = new OptionalNullableDto("Hello", null, true, null, null, true);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":null,\"prop4\":null}", output);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void serialize_when_prop4Present_then_correctSerialized(boolean isProp4Null)
      throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();

    final OptionalNullableDto dto =
        new OptionalNullableDto("Hello", null, true, null, "World", isProp4Null);

    final String output = mapper.writeValueAsString(dto);

    assertEquals("{\"prop1\":\"Hello\",\"prop2\":null,\"prop4\":\"World\"}", output);
  }
}
