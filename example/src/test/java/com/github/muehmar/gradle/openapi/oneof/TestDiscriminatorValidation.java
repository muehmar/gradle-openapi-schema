package com.github.muehmar.gradle.openapi.oneof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.oneof.model.AdminOrUserDiscriminatorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class TestDiscriminatorValidation {

  private static final ValidatorFactory VALIDATOR_FACTORY =
      Validation.buildDefaultValidatorFactory();
  private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        VALIDATOR.validate(adminOrUserDto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation()
      throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\",\"age\":200,\"email\":null}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        VALIDATOR.validate(adminOrUserDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<AdminOrUserDiscriminatorDto> violation = violations.iterator().next();
    assertEquals("oneOf.ageRaw", violation.getPropertyPath().toString());
  }

  @Test
  void validate_when_matchesNoSchema_then_violations() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue("{}", AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        VALIDATOR.validate(adminOrUserDto);

    // Violation cause no schema matches and cause of invalid discriminator
    assertEquals(2, violations.size());
    final String joinedPropertyPaths =
        new ArrayList<>(violations)
            .stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.joining(" , "));
    assertEquals("validAgainstNoSchema , validAgainstTheCorrectSchema", joinedPropertyPaths);
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_violation() throws JsonProcessingException {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"type\":\"User\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        VALIDATOR.validate(adminOrUserDto);

    assertEquals(1, violations.size());
    final ConstraintViolation<AdminOrUserDiscriminatorDto> violation = violations.iterator().next();
    assertEquals("validAgainstMoreThanOneSchema", violation.getPropertyPath().toString());
  }
}
