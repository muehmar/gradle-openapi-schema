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

public class UsersValidationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_everythingOk_then_noViolations() throws Exception {
    final String json = "{\"ids\":[\"id-1234\"],\"usernames\":null}";

    final ListObjectDto listObjectDto = MAPPER.readValue(json, ListObjectDto.class);

    final Set<ConstraintViolation<ListObjectDto>> violations = validate(listObjectDto);

    assertEquals(0, violations.size());
    assertTrue(listObjectDto.isValid());
  }

  @Test
  void validate_when_idsHasTooMuchItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null, null],\"usernames\":null}";

    final ListObjectDto listObjectDto = MAPPER.readValue(json, ListObjectDto.class);

    final Set<ConstraintViolation<ListObjectDto>> violations = validate(listObjectDto);

    assertEquals(
        list("idsRaw -> size must be between 1 and 2"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(listObjectDto.isValid());
  }

  @Test
  void validate_when_usernamesHasToFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":[]}";

    final ListObjectDto listObjectDto = MAPPER.readValue(json, ListObjectDto.class);

    final Set<ConstraintViolation<ListObjectDto>> violations = validate(listObjectDto);

    assertEquals(
        list("usernames -> size must be between 3 and 4"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(listObjectDto.isValid());
  }

  @Test
  void validate_when_emailsHasToFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":null,\"emails\":[]}";

    final ListObjectDto listObjectDto = MAPPER.readValue(json, ListObjectDto.class);

    final Set<ConstraintViolation<ListObjectDto>> violations = validate(listObjectDto);

    assertEquals(
        list("emails -> size must be between 5 and 6"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(listObjectDto.isValid());
  }

  @Test
  void validate_when_phonesHasTooFewItems_then_violation() throws Exception {
    final String json = "{\"ids\":[\"id-1234\", null],\"usernames\":null,\"phones\":[]}";

    final ListObjectDto listObjectDto = MAPPER.readValue(json, ListObjectDto.class);

    final Set<ConstraintViolation<ListObjectDto>> violations = validate(listObjectDto);

    assertEquals(
        list("phones -> size must be between 7 and 8"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(listObjectDto.isValid());
  }
}
