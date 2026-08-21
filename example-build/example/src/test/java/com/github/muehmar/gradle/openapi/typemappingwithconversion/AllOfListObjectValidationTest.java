package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Lists.list;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class AllOfListObjectValidationTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_everythingOk_then_noViolations() throws Exception {
    final String json = "{\"ids\":[\"id-1234\"],\"usernames\":null}";

    final AllOfListObjectDto allOfListObjectDto = MAPPER.readValue(json, AllOfListObjectDto.class);

    final Set<ConstraintViolation<AllOfListObjectDto>> violations = validate(allOfListObjectDto);

    assertEquals(0, violations.size());
    assertTrue(allOfListObjectDto.isValid());
  }

  @Test
  void validate_when_idsHasTooMuchItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null, null],\"usernames\":null}";

    final AllOfListObjectDto allOfListObjectDto = MAPPER.readValue(json, AllOfListObjectDto.class);

    final Set<ConstraintViolation<AllOfListObjectDto>> violations = validate(allOfListObjectDto);

    assertEquals(
        list("listObjectDto.idsRaw -> size must be between 1 and 2"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(allOfListObjectDto.isValid());
  }

  @Test
  void validate_when_usernamesHasToFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":[]}";

    final AllOfListObjectDto allOfListObjectDto = MAPPER.readValue(json, AllOfListObjectDto.class);

    final Set<ConstraintViolation<AllOfListObjectDto>> violations = validate(allOfListObjectDto);

    assertEquals(
        list("listObjectDto.usernames -> size must be between 3 and 4"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(allOfListObjectDto.isValid());
  }

  @Test
  void validate_when_emailsHasToFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":null,\"emails\":[]}";

    final AllOfListObjectDto allOfListObjectDto = MAPPER.readValue(json, AllOfListObjectDto.class);

    final Set<ConstraintViolation<AllOfListObjectDto>> violations = validate(allOfListObjectDto);

    assertEquals(
        list("listObjectDto.emails -> size must be between 5 and 6"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(allOfListObjectDto.isValid());
  }

  @Test
  void validate_when_phonesHasTooFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":null,\"phones\":[]}";

    final AllOfListObjectDto allOfListObjectDto = MAPPER.readValue(json, AllOfListObjectDto.class);

    final Set<ConstraintViolation<AllOfListObjectDto>> violations = validate(allOfListObjectDto);

    assertEquals(
        list("listObjectDto.phones -> size must be between 7 and 8"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(allOfListObjectDto.isValid());
  }
}
