package com.github.muehmar.gradle.openapi.nullability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.nullability.model.UserDto;
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
  void validate_when_minimalValidDto_then_noViolations() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"123abc\",\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_fullValidDto_then_noViolations() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"Dexter\",\"email\":\"hello@github.com\",\"phone\":\"+419998877\"}",
            UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_idNotPresent_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_idTooShort_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"1a\",\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_usernameNotPresent_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"123abc\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_usernameTooLong_then_singleViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"usernameusernameusername\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_emailDoesNotMatchPattern_then_singleViolation()
      throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":null,\"email\":\"invalid\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_emailIsNull_then_noViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue("{\"id\":\"123abc\",\"username\":null,\"email\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_phoneDoesNotMatchPattern_then_singleViolation()
      throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":null,\"phone\":\"+4112\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(1, violations.size());
    violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
  }

  @Test
  void validate_when_phoneIsNull_then_noViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue("{\"id\":\"123abc\",\"username\":null,\"phone\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = VALIDATOR.validate(dto);
    assertEquals(0, violations.size());
  }
}
