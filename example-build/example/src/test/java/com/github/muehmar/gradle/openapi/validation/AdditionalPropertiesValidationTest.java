package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class AdditionalPropertiesValidationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_onlyDefinedPropertiesPresent_then_noViolations() throws Exception {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"val1\":\"value\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> constraintViolations = validate(dto);

    assertEquals(0, constraintViolations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertiesPresent_then_violation() throws Exception {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"val1\":\"value\",\"val2\":\"\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> constraintViolations = validate(dto);

    assertEquals(1, constraintViolations.size());
    assertEquals(
        "No additional properties allowed",
        constraintViolations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }
}
