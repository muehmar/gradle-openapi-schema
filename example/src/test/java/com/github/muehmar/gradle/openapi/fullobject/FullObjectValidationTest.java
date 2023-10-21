package com.github.muehmar.gradle.openapi.fullobject;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class FullObjectValidationTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_allOk_then_noViolations() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void getPropertyCount_when_called_then_correctPropertyCountReturned()
      throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    assertEquals(7, dto.getPropertyCount());
  }

  @Test
  void validate_when_invalidAdditionalPropertiesTypeForAdminDto_then_violation()
      throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"Admin\",\"adminname\":\"adminname\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidCompositionDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidCompositionDtos[1].username -> must not be null",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_tooMuchProperties_then_violation() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\",\"too-much\":\"properties\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidCompositionDtos[0].adminname -> must not be null",
            "invalidCompositionDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidCompositionDtos[1].propertyCount -> must be less than or equal to 7",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_wrongDiscriminator_then_violation() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"Admin\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_requiredAllOfMemberMissing_then_violation() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"type\":\"Admin\",\"adminname\":\"adminname\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must not be null", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_propertyTooLong_then_violation() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message-too-long\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 0 and 10", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_anyOfPropertyTooLong_then_violation() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username-too-long\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidCompositionDtos[0].adminname -> must not be null",
            "invalidCompositionDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidCompositionDtos[1].username -> size must be between 0 and 9",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }
}
