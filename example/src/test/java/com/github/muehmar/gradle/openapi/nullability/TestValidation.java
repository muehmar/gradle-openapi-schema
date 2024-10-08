package com.github.muehmar.gradle.openapi.nullability;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class TestValidation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void validate_when_minimalValidDto_then_noViolations() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"123abc\",\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_fullValidDto_then_noViolations() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"Dexter\",\"email\":\"hello@github.com\",\"phone\":\"+419998877\"}",
            UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_idNotPresent_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("id -> must not be null"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_idTooShort_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"1a\",\"username\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("id -> size must be between 6 and 10"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_usernameNotPresent_then_singleViolation() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"123abc\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("usernamePresent -> username is required but it is not present"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_usernameTooLong_then_singleViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"usernameusernameusername\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("username -> size must be between 5 and 20"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_emailDoesNotMatchPattern_then_singleViolation()
      throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":null,\"email\":\"invalid\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("email -> must match \"[A-Za-z0-9]+@[A-Za-z0-9]+\\.[a-z]+\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_emailIsNull_then_singleViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue("{\"id\":\"123abc\",\"username\":null,\"email\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("emailNotNull -> email is required to be non-null but is null"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_phoneDoesNotMatchPattern_then_singleViolation()
      throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":null,\"phone\":\"+4112\"}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("phone -> must match \"\\+41[0-9]{7}\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_phoneIsNull_then_noViolation() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue("{\"id\":\"123abc\",\"username\":null,\"phone\":null}", UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }
}
