package com.github.muehmar.gradle.openapi.issues.issue229;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class DiscriminatorValidationTest {

  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"user\",\"username\":\"user-name\"}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesNoSchema_then_violations() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto = MAPPER.readValue("{}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[Admin].adminname -> must not be null",
            "invalidOneOf[Admin].usertype -> must not be null",
            "invalidOneOf[User].username -> must not be null",
            "invalidOneOf[User].usertype -> must not be null",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [User, Admin]",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_violation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"user\",\"username\":\"user-name\",\"adminname\":\"admin-name\"}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "validAgainstMoreThanOneSchema -> Is valid against more than one of the schemas [User, Admin]"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }
}
