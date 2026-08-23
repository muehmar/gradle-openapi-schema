package com.github.muehmar.gradle.openapi.fullobject;

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

class FullObjectValidationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_allOk_then_noViolations() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void getPropertyCount_when_called_then_correctPropertyCountReturned() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    assertEquals(7, dto.getPropertyCount());
  }

  @Test
  void validate_when_enumValuedAdditionalPropertyForAdminDto_then_noViolation() throws Exception {
    // The enum property 'color' of the BaseData is represented internally as a String. When the
    // oneOf is matched against the Admin schema (whose additional properties are of type string)
    // the value is therefore a valid additional property and no type violation is raised.
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"Admin\",\"adminname\":\"adminname\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(Arrays.asList(), formatViolations(violations));
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_tooMuchProperties_then_violation() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\",\"too-much\":\"properties\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[User].propertyCount -> must be less than or equal to 8",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_wrongDiscriminator_then_violation() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"Admin\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[Admin].adminname_ -> must not be null",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_requiredAllOfMemberMissing_then_violation() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"type\":\"Admin\",\"adminname\":\"adminname\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must not be null", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_propertyTooLong_then_violation() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message-too-long\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList("message_ -> size must be between 0 and 10"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_anyOfPropertyTooLong_then_violation() throws Exception {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"route\":\"route\",\"schema\":\"schema\",\"color\":\"red\",\"type\":\"User\",\"username\":\"username-too-long\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    final Set<ConstraintViolation<FullObjectDto>> violations = validate(dto);

    assertEquals(
        Arrays.asList(
            "invalidOneOf[User].username_ -> size must be between 0 and 9",
            "validAgainstNoOneOfSchema -> Is not valid against one of the schemas [Admin, User]",
            "validAgainstTheCorrectOneOfSchema -> Not valid against the schema described by the oneOf-discriminator"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(dto.isValid());
  }
}
