package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class ArrayAdditionalPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();
  private String json;

  @Test
  void validate_when_validAdditionalProperty_then_noViolationsAndValueReturned()
      throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"martin\",\"data\":[\"hello\",\"world\"]}",
            ArrayAdditionalPropertiesDto.class);

    assertEquals(
        Optional.of(Arrays.asList("hello", "world")),
        dto.getAdditionalProperty("data").map(ArrayAdditionalPropertiesPropertyDto::getItems));

    final Set<ConstraintViolation<ArrayAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_arrayWithTooManyItems_then_violation() throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"martin\",\"data\":[\"hello\",\"world\",\"!\"]}",
            ArrayAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<ArrayAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 1 and 2", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void serialize_when_withArrayAsAdditionalProperty_then_correctJson()
      throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        ArrayAdditionalPropertiesDto.builder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty(
                "hello",
                new ArrayAdditionalPropertiesPropertyDto(Collections.singletonList("world")))
            .build();

    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"name\":\"name\",\"hello\":[\"world\"]}", json);
  }

  @Test
  void deserialize_when_withArrayAsAdditionalProperty_then_correctDto()
      throws JsonProcessingException {
    final String json = "{\"name\":\"name\",\"hello\":[\"world\"]}";

    final ArrayAdditionalPropertiesDto dto =
        MAPPER.readValue(json, ArrayAdditionalPropertiesDto.class);

    final ArrayAdditionalPropertiesDto expectedDto =
        ArrayAdditionalPropertiesDto.builder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty(
                "hello",
                new ArrayAdditionalPropertiesPropertyDto(Collections.singletonList("world")))
            .build();

    assertEquals(expectedDto, dto);
  }
}
