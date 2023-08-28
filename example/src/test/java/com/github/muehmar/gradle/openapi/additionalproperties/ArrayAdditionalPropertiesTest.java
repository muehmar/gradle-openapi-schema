package com.github.muehmar.gradle.openapi.additionalproperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.additionalproperties.model.ArrayAdditionalPropertiesDto;
import openapischema.example.api.additionalproperties.model.ArrayAdditionalPropertiesPropertyDto;
import org.junit.jupiter.api.Test;

class ArrayAdditionalPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_validAdditionalProperty_then_noViolationsAndValueReturned()
      throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"martin\",\"data\":[\"hello\",\"world\"]}",
            ArrayAdditionalPropertiesDto.class);

    assertEquals(
        Optional.of(new ArrayList<>(Arrays.asList("hello", "world"))),
        dto.getAdditionalProperty("data").map(ArrayAdditionalPropertiesPropertyDto::getValue));

    final Set<ConstraintViolation<ArrayAdditionalPropertiesDto>> violations =
        ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_arrayWithTooManyItems_then_violation() throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"martin\",\"data\":[\"hello\",\"world\",\"!\"]}",
            ArrayAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<ArrayAdditionalPropertiesDto>> violations =
        ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 1 and 2", violations.stream().findFirst().get().getMessage());
  }

  @Test
  void serialize_when_withArrayAsAdditionalProperty_then_correctJson()
      throws JsonProcessingException {
    final ArrayAdditionalPropertiesDto dto =
        ArrayAdditionalPropertiesDto.newBuilder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty(
                "hello",
                new ArrayAdditionalPropertiesPropertyDto(
                    new ArrayList<>(Collections.singletonList("world"))))
            .build();

    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"name\":\"name\",\"hello\":[\"world\"]}", json);
  }
}
