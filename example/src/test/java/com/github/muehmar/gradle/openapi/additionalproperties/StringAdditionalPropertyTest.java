package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringAdditionalPropertyTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();
  private String json;

  @Test
  void validate_when_validAdditionalProperty_then_noViolationsAndValueReturned()
      throws JsonProcessingException {
    final StringAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"hello\",\"message\":\"world\"}", StringAdditionalPropertiesDto.class);

    assertEquals(Optional.of("world"), dto.getAdditionalProperty("message"));

    final Set<ConstraintViolation<StringAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_invalidPatternAdditionalProperty_then_violation()
      throws JsonProcessingException {
    final StringAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"hello\",\"message\":\"world!\"}", StringAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<StringAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must match \"[A-Za-z0-9]+\"", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_toLongStringAdditionalProperty_then_violation()
      throws JsonProcessingException {
    final StringAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"hello\",\"message\":\"worldworldworld\"}",
            StringAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<StringAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 0 and 10", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4})
  void validate_when_differentAdditionalPropertyCount_then_violationIfInvalidPropertyCount(
      int additionalPropertyCount) {

    final HashMap<String, String> additionalProperties = new HashMap<>();
    for (int i = 0; i < additionalPropertyCount; i++) {
      additionalProperties.put(String.format("prop%d", i), String.format("value%d", i));
    }

    final StringAdditionalPropertiesDto dto =
        StringAdditionalPropertiesDto.builder()
            .setName("Dexter")
            .andOptionals()
            .setAdditionalProperties(additionalProperties)
            .build();

    final Set<ConstraintViolation<StringAdditionalPropertiesDto>> violations = validate(dto);

    if (additionalPropertyCount < 1) {
      assertEquals(1, violations.size());
      assertEquals(
          "must be greater than or equal to 2", violations.stream().findFirst().get().getMessage());
      assertFalse(dto.isValid());
    } else if (3 < additionalPropertyCount) {
      assertEquals(1, violations.size());
      assertEquals(
          "must be less than or equal to 4", violations.stream().findFirst().get().getMessage());
      assertFalse(dto.isValid());
    } else {
      assertEquals(0, violations.size());
      assertTrue(dto.isValid());
    }
  }

  @Test
  void serialize_when_dtoWithAdditionalProperties_then_correctJson()
      throws JsonProcessingException {
    final StringAdditionalPropertiesDto dto =
        StringAdditionalPropertiesDto.builder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("HELLO", "WORLD")
            .build();
    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"name\":\"name\",\"HELLO\":\"WORLD\",\"hello\":\"world\"}", json);
  }

  @Test
  void deserialize_when_jsonWithAdditionalProperties_then_correctDto()
      throws JsonProcessingException {
    final String json = "{\"name\":\"name\",\"HELLO\":\"WORLD\",\"hello\":\"world\"}";

    final StringAdditionalPropertiesDto dto =
        MAPPER.readValue(json, StringAdditionalPropertiesDto.class);

    final StringAdditionalPropertiesDto expectedDto =
        StringAdditionalPropertiesDto.builder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("HELLO", "WORLD")
            .build();

    assertEquals(expectedDto, dto);
  }
}
