package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class RequiredAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void builder_when_createAndSerialized_then_correctJson() throws Exception {
    final RequiredAdditionalPropertiesDto dto =
        RequiredAdditionalPropertiesDto.builder()
            .setName("name")
            .setLastname("lastname")
            .andAllOptionals()
            .addAdditionalProperty("street", "waldweg")
            .build();

    assertEquals(
        "{\"name\":\"name\",\"street\":\"waldweg\",\"lastname\":\"lastname\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void fullBuilder_when_createAndSerialized_then_correctJson() throws Exception {
    final RequiredAdditionalPropertiesDto dto =
        RequiredAdditionalPropertiesDto.fullBuilder()
            .setName("name")
            .setLastname("lastname")
            .addAdditionalProperty("street", "waldweg")
            .build();

    assertEquals(
        "{\"name\":\"name\",\"street\":\"waldweg\",\"lastname\":\"lastname\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final RequiredAdditionalPropertiesDto exptectedDto =
        RequiredAdditionalPropertiesDto.fullBuilder()
            .setName("name")
            .setLastname("lastname")
            .addAdditionalProperty("street", "waldweg")
            .build();

    final RequiredAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"name\",\"street\":\"waldweg\",\"lastname\":\"lastname\"}",
            RequiredAdditionalPropertiesDto.class);

    assertEquals(exptectedDto, dto);
  }

  @Test
  void validate_when_requiredAdditionalPropertyMissing_then_violation() throws Exception {
    final RequiredAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"name\",\"street\":\"waldweg\"}", RequiredAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<RequiredAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must not be null", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_requiredAdditionalPropertyTooShort_then_violation() throws Exception {
    final RequiredAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"name\",\"street\":\"waldweg\",\"lastname\":\"ln\"}",
            RequiredAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<RequiredAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 5 and 2147483647",
        violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }
}
