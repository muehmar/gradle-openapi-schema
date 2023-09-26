package com.github.muehmar.gradle.openapi.oneof;

import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import openapischema.example.api.oneof.model.AdminOrUserDto;
import org.junit.jupiter.api.Test;

class ValidationTest {

  private static final ValidatorFactory VALIDATOR_FACTORY =
      Validation.buildDefaultValidatorFactory();
  private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"type\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation()
      throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"type\",\"username\":\"user-name\",\"age\":200,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<AdminOrUserDto> violation = violations.iterator().next();
    assertEquals("oneOf.ageRaw", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_matchesNoSchema_then_violation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto = MAPPER.readValue("{}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidCompositionDtos[0].adminname -> must not be null",
            "invalidCompositionDtos[0].id -> must not be null",
            "invalidCompositionDtos[0].type -> must not be null",
            "invalidCompositionDtos[1].id -> must not be null",
            "invalidCompositionDtos[1].type -> must not be null",
            "invalidCompositionDtos[1].username -> must not be null",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]"),
        formatViolations(violations));
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_violation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id-123\",\"type\":\"type\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "validAgainstMoreThanOneSchema -> Is valid against more than one of the schemas [Admin, User]"),
        formatViolations(violations));
  }
}
