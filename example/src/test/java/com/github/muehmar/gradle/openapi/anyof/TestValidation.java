package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.anyof.model.AdminOrUserDto;
import OpenApiSchema.example.api.anyof.model.InlinedAnyOfDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class TestValidation {

  private static final ValidatorFactory VALIDATOR_FACTORY =
      Validation.buildDefaultValidatorFactory();
  private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_matchesUserSchemaOfInlineDto_then_noViolation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedAnyOfDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations =
        VALIDATOR.validate(inlinedAnyOfDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation()
      throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":200,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<AdminOrUserDto> violation = violations.iterator().next();
    assertEquals("anyOf[0].ageRaw", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAgeOfInlinedDto_then_violation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":200,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = VALIDATOR.validate(inlinedDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<InlinedAnyOfDto> violation = violations.iterator().next();
    assertEquals("adminOrUser.anyOf[0].ageRaw", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_matchesNoSchema_then_violation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto = MAPPER.readValue("{}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<AdminOrUserDto> violation = violations.iterator().next();
    assertEquals("validAgainstNoSchema", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_matchesNoSchemaOfInlinedDto_then_violation() throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue("{\"adminOrUser\":{}}", InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = VALIDATOR.validate(inlinedDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<InlinedAnyOfDto> violation = violations.iterator().next();
    assertEquals("adminOrUser.validAgainstNoSchema", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = VALIDATOR.validate(adminOrUserDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_doesMatchBothSchemasOfInlinedDto_then_noViolation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"id\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = VALIDATOR.validate(inlinedDto);

    assertEquals(0, violations.size());
  }
}
