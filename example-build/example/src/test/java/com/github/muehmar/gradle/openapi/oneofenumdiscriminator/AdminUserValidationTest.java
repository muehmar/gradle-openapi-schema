package com.github.muehmar.gradle.openapi.oneofenumdiscriminator;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class AdminUserValidationTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_matchesUserSchema_then_noViolation() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"user\",\"username\":\"user-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(0, violations.size());
    assertTrue(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesUserSchemaButInvalidAge_then_violation() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"user-id\",\"type\":\"user\",\"username\":\"user-name\",\"age\":200,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[User].age -> must be less than or equal to 199",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_matchesNoSchema_then_violations() throws Exception {
    final AdminOrUserDto adminOrUserDto = MAPPER.readValue("{}", AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[Admin].adminname -> must not be null",
            "invalidOneOf[Admin].baseUserDto.type -> must not be null",
            "invalidOneOf[Admin].id -> must not be null",
            "invalidOneOf[User].baseUserDto.type -> must not be null",
            "invalidOneOf[User].id -> must not be null",
            "invalidOneOf[User].username -> must not be null",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }

  @Test
  void validate_when_doesMatchBothSchemas_then_violation() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"id\":\"id-123\",\"type\":\"user\",\"username\":\"user-name\",\"adminname\":\"admin-name\",\"age\":25,\"email\":null}",
            AdminOrUserDto.class);

    final Set<ConstraintViolation<AdminOrUserDto>> violations = validate(adminOrUserDto);

    assertEquals(
        Collections.singletonList(
            "validAgainstMoreThanOneSchema -> Is valid against more than one of the schemas [Admin, User]"),
        formatViolations(violations));
    assertFalse(adminOrUserDto.isValid());
  }
}
