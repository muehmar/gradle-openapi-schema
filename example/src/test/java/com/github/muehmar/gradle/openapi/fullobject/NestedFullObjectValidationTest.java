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

class NestedFullObjectValidationTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_allOk_then_noViolations() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void getPropertyCount_when_called_then_correctPropertyCountReturned()
      throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    assertEquals(7, dto.getPropertyCount());
  }

  @Test
  void validate_when_invalidAdditionalPropertiesTypeForAdminDto_then_violation()
      throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"Admin\",\"adminname\":\"adminname\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].invalidOneOfDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidAnyOfDtos[0].invalidOneOfDtos[1].username -> must not be null",
            "invalidAnyOfDtos[0].validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "invalidAnyOfDtos[0].validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_tooMuchProperties_then_violation() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"hello\":\"world!\",\"too-much\":\"properties\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].invalidOneOfDtos[0].adminname -> must not be null",
            "invalidAnyOfDtos[0].invalidOneOfDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidAnyOfDtos[0].invalidOneOfDtos[1].propertyCount -> must be less than or equal to 7",
            "invalidAnyOfDtos[0].validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "invalidAnyOfDtos[0].validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_wrongDiscriminator_then_violation() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"Admin\",\"username\":\"username\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_requiredAllOfMemberMissing_then_violation() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].baseDataDto.color -> must not be null",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_propertyTooLong_then_violation() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message-too-long\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].messageRaw -> size must be between 0 and 10",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_anyOfPropertyTooLong_then_violation() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"amount\":15,\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username-too-long\",\"message\":\"message\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    final Set<ConstraintViolation<NestedFullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].invalidOneOfDtos[0].adminname -> must not be null",
            "invalidAnyOfDtos[0].invalidOneOfDtos[0].allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String",
            "invalidAnyOfDtos[0].invalidOneOfDtos[1].username -> size must be between 0 and 9",
            "invalidAnyOfDtos[0].validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "invalidAnyOfDtos[0].validAgainstTheCorrectSchema -> Not valid against the schema described by the discriminator",
            "invalidAnyOfDtos[1].membername -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [FullObject, Member]"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }
}
