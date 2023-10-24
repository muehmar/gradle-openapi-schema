package com.github.muehmar.gradle.openapi.anyof;

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

class ValidationTest {

  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesUserSchemaOfInlineDto_then_noViolation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedAnyOfDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = validate(inlinedAnyOfDto);

    assertEquals(0, violations.size());
    assertTrue(inlinedAnyOfDto.isValid());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation()
      throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":200,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].adminname -> must not be null",
            "invalidAnyOfDtos[1].ageRaw -> must be less than or equal to 199",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAgeOfInlinedDto_then_violation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":200,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = validate(inlinedDto);

    assertEquals(
        Arrays.asList(
            "adminOrUser.invalidAnyOfDtos[0].adminname -> must not be null",
            "adminOrUser.invalidAnyOfDtos[1].ageRaw -> must be less than or equal to 199",
            "adminOrUser.validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(inlinedDto.isValid());
  }

  @Test
  void validate_when_matchesNoSchema_then_violation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto = MAPPER.readValue("{}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOfDtos[0].adminname -> must not be null",
            "invalidAnyOfDtos[0].id -> must not be null",
            "invalidAnyOfDtos[1].id -> must not be null",
            "invalidAnyOfDtos[1].username -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesNoSchemaOfInlinedDto_then_violation() throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue("{\"adminOrUser\":{}}", InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = validate(inlinedDto);

    assertEquals(
        Arrays.asList(
            "adminOrUser.invalidAnyOfDtos[0].adminname -> must not be null",
            "adminOrUser.invalidAnyOfDtos[0].id -> must not be null",
            "adminOrUser.invalidAnyOfDtos[1].id -> must not be null",
            "adminOrUser.invalidAnyOfDtos[1].username -> must not be null",
            "adminOrUser.validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]"),
        formatViolations(violations));
    assertFalse(inlinedDto.isValid());
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_noViolation() throws JsonProcessingException {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_doesMatchBothSchemasOfInlinedDto_then_noViolation()
      throws JsonProcessingException {
    final InlinedAnyOfDto inlinedDto =
        MAPPER.readValue(
            "{\"adminOrUser\":{\"id\":\"id\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}}",
            InlinedAnyOfDto.class);

    final Set<ConstraintViolation<InlinedAnyOfDto>> violations = validate(inlinedDto);

    assertEquals(0, violations.size());
    assertTrue(inlinedDto.isValid());
  }
}
