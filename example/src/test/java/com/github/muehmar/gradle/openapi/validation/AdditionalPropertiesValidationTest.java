package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.validation.model.NoAdditionalPropertiesDto;
import org.junit.jupiter.api.Test;

class AdditionalPropertiesValidationTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_onlyDefinedPropertiesPresent_then_noViolations()
      throws JsonProcessingException {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"val1\":\"value\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
  }

  @Test
  void validate_when_additionalPropertiesPresent_then_violation() throws JsonProcessingException {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"val1\":\"value\",\"val2\":\"\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "No additional properties allowed",
        constraintViolations.stream().findFirst().get().getMessage());
  }
}
