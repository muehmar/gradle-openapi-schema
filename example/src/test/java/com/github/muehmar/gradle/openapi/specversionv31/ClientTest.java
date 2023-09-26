package com.github.muehmar.gradle.openapi.specversionv31;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.specversionv31.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ClientTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void serialize_when_noName_then_serializedAsNull() throws JsonProcessingException {
    final ClientDto dto = ClientDto.builder().setId("id").setName(Optional.empty()).build();
    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"id\":\"id\",\"name\":null}", json);
  }

  @ParameterizedTest
  @ValueSource(floats = {120.6f, 180f, 200f})
  void validate_when_heightInRange_then_noViolations(float height) {
    final ClientDto dto =
        ClientDto.builder().setId("id").setName("name").andOptionals().setHeight(height).build();

    final Set<ConstraintViolation<ClientDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(floats = {50f, 120.5f, 200.1f, 300})
  void validate_when_heightExceedsRange_then_violations(float height) {
    final ClientDto dto =
        ClientDto.builder().setId("id").setName("name").andOptionals().setHeight(height).build();

    final Set<ConstraintViolation<ClientDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {18, 50, 99})
  void validate_when_heightInRange_then_noViolations(int age) {
    final ClientDto dto =
        ClientDto.builder().setId("id").setName("name").andOptionals().setAge(age).build();

    final Set<ConstraintViolation<ClientDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 17, 100, 200})
  void validate_when_heightExceedsRange_then_violations(int age) {
    final ClientDto dto =
        ClientDto.builder().setId("id").setName("name").andOptionals().setAge(age).build();

    final Set<ConstraintViolation<ClientDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
  }
}
