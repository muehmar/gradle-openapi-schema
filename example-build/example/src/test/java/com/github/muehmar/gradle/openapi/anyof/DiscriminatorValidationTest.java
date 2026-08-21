package com.github.muehmar.gradle.openapi.anyof;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class DiscriminatorValidationTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":25,\"email\":null,\"type\":\"User\"}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"username\":\"user-name\",\"age\":200,\"email\":null,\"type\":\"User\"}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOf[User].age -> must be less than or equal to 199",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectAnyOfSchema -> Not valid against the schema described by the anyOf-discriminator"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesNoSchema_then_violation() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue("{}", AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidAnyOf[Admin].adminname -> must not be null",
            "invalidAnyOf[Admin].id -> must not be null",
            "invalidAnyOf[Admin].type -> must not be null",
            "invalidAnyOf[User].id -> must not be null",
            "invalidAnyOf[User].type -> must not be null",
            "invalidAnyOf[User].username -> must not be null",
            "validAgainstNoAnyOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectAnyOfSchema -> Not valid against the schema described by the anyOf-discriminator"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_noViolation() throws Exception {
    final AdminOrUserDiscriminatorDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null,\"type\":\"Admin\"}",
            AdminOrUserDiscriminatorDto.class);

    final Set<ConstraintViolation<AdminOrUserDiscriminatorDto>> violations =
        validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }
}
