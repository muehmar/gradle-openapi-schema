package com.github.muehmar.gradle.openapi.nullableitemslist;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ValidationTest {
  private static final ObjectMapper OBJECT_MAPPER = MapperFactory.mapper();

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[\"id-1234\"]",
        "[\"id-1234\",\"id-1235\"]",
        "[\"id-123\",\"id-456\"]",
        "[\"id-1234567\",\"id-7654321\"]",
        "[null]",
        "[null, null]",
        "[null, \"id-1234\"]",
        "[\"id-1234\", null]",
      })
  void validate_when_validIds_then_noViolations(String idsJson) throws JsonProcessingException {
    final String json = String.format("{\"ids\":%s,\"usernames\":null}", idsJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "null",
        "[]",
        "[\"id-12\"]",
        "[\"id-12\", null]",
        "[\"id-12345678\", null]",
        "[\"id-1234\", \"id-1235\", \"id-1236\"]",
        "[null, null, null]",
      })
  void validate_when_invalidIds_then_violations(String idsJson) throws JsonProcessingException {
    final String json = String.format("{\"ids\":%s,\"usernames\":null}", idsJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(1, violations.size());
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_idsIsNull_then_violationWithCorrectMessage() throws JsonProcessingException {
    final String json = "{\"ids\":null,\"usernames\":null}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("idsRaw -> must not be null"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_toManyIds_then_violationWithCorrectMessage() throws JsonProcessingException {
    final String json = "{\"ids\":[\"id-1234\", \"id-1235\", \"id-1236\"],\"usernames\":null}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("idsRaw -> size must be between 1 and 2"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_invalidIds_then_violationWithCorrectMessage() throws JsonProcessingException {
    final String json = "{\"ids\":[\"id-1234\", \"id-123456789\"],\"usernames\":null}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("idsRaw[1].<list element> -> size must be between 6 and 10"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "null",
        "[\"user-1234\",\"user-1235\",\"user-1236\"]",
        "[\"user-1234\",\"user-1235\",\"user-1236\",\"user-1237\"]",
        "[\"user-1234567\",\"user-4567890\", null]",
        "[null, null, null]",
        "[null, null, null, null]",
        "[null, \"user-1234\", null]",
        "[\"user-1234\", null, \"user-1235\", null]",
      })
  void validate_when_validUsernames_then_noViolations(String usernamesJson)
      throws JsonProcessingException {
    final String json = String.format("{\"ids\":[null],\"usernames\":%s}", usernamesJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[]",
        "[\"user-1234\", \"user-1235\"]",
        "[\"user-123\", null, null]",
        "[\"user-123456780\", null, null]",
        "[\"user-1234\", \"user-1235\", \"user-1236\", \"user-1237\", \"user-1238\"]",
        "[null, null, null, null, null]",
      })
  void validate_when_invalidUsernames_then_violations(String usernamesJson)
      throws JsonProcessingException {
    final String json = String.format("{\"ids\":[null],\"usernames\":%s}", usernamesJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(1, violations.size());
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_usernamesAbsent_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json = "{\"ids\":[null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList(
            "usernamesPresent -> usernames is required but it is not present"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_toManyUsernames_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json = "{\"ids\":[null],\"usernames\":[null, null, null, null, null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("usernames -> size must be between 3 and 4"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_invalidUsernames_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json = "{\"ids\":[null],\"usernames\":[null, \"user-123456780\", null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("usernames[1].<list element> -> size must be between 9 and 12"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[\"email-1234\",\"email-1235\",\"email-1236\",\"email-1237\",\"email-1238\"]",
        "[\"email-1234\",\"email-1235\",\"email-1236\",\"email-1237\",\"email-1238\",\"email-1239\"]",
        "[\"email-1234\",null,null,null,\"email-1238\"]",
        "[\"email-1234\",null,null,\"email-9876\",null,\"email-1238\"]",
        "[null,null,null,null,null]",
        "[null,null,null,null,null,null]"
      })
  void validate_when_validEmails_then_noViolations(String emailsJson)
      throws JsonProcessingException {
    final String json =
        String.format("{\"ids\":[null],\"usernames\":null,\"emails\":%s}", emailsJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "null",
        "[]",
        "[\"email-1234\",\"email-1235\",\"email-1236\",\"email-1237\"]",
        "[\"email-1234\",\"email-1235\",\"email-1236\",\"email-1237\",\"email-1238\",\"email-1239\",\"email-1230\"]",
        "[null, null, null, null]",
        "[null, null, null, null, null, null, null]",
        "[\"email-123\", null, null, null, null]"
      })
  void validate_when_invalidEmails_then_violations(String emailsJson)
      throws JsonProcessingException {
    final String json =
        String.format("{\"ids\":[null],\"usernames\":null,\"emails\":%s}", emailsJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(1, violations.size());
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_toFewEmails_then_violationWithCorrectMessage() throws JsonProcessingException {
    final String json = "{\"ids\":[null],\"usernames\":null,\"emails\":[null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("emails -> size must be between 5 and 6"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_emailsIsNull_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json = "{\"ids\":[null],\"usernames\":null,\"emails\":null}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("emailsNotNull -> emails is required to be non-null but is null"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_invalidEmails_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json =
        "{\"ids\":[null],\"usernames\":null,\"emails\":[\"email-123456780\",null,null,null,null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("emails[0].<list element> -> must match \"[a-z]+-[0-9]{4}\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "null",
        "[\"phone-1234\",\"phone-1235\",\"phone-1236\",\"phone-1237\",\"phone-1238\",\"phone-1239\",\"phone-1230\"]",
        "[\"phone-1234\",\"phone-1235\",\"phone-1236\",\"phone-1237\",\"phone-1238\",\"phone-1239\",\"phone-1230\",\"phone-1231\"]",
        "[\"phone-1234\",null,null,null,null,null,\"phone-1238\"]",
        "[\"phone-1234\",null,null,\"phone-9876\",null,null,null,\"phone-1238\"]",
        "[null,null,null,null,null,null,null]",
        "[null,null,null,null,null,null,null,null]"
      })
  void validate_when_validPhones_then_noViolations(String phonesJson)
      throws JsonProcessingException {
    final String json =
        String.format("{\"ids\":[null],\"usernames\":null,\"phones\":%s}", phonesJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[]",
        "[\"phone-1234\",\"phone-1235\",\"phone-1236\",\"phone-1237\",\"phone-1238\",\"phone-1239\"]",
        "[\"phone-1234\",\"phone-1235\",\"phone-1236\",\"phone-1237\",\"phone-1238\",\"phone-1239\",\"phone-1230\",\"phone-1231\",\"phone-1232\"]",
        "[null, null, null, null, null, null]",
        "[null, null, null, null, null, null, null, null, null]",
        "[\"phone-123\", null, null, null, null, null, null]"
      })
  void validate_when_invalidPhones_then_violations(String phonesJson)
      throws JsonProcessingException {
    final String json =
        String.format("{\"ids\":[null],\"usernames\":null,\"phones\":%s}", phonesJson);
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(1, violations.size());
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_toFewPhones_then_violationWithCorrectMessage() throws JsonProcessingException {
    final String json = "{\"ids\":[null],\"usernames\":null,\"phones\":[null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("phones -> size must be between 7 and 8"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }

  @Test
  void validate_when_invalidPhones_then_violationWithCorrectMessage()
      throws JsonProcessingException {
    final String json =
        "{\"ids\":[null],\"usernames\":null,\"phones\":[\"phone-123456780\",null,null,null,null,null,null]}";
    final UserDto userDto = OBJECT_MAPPER.readValue(json, UserDto.class);

    final Set<ConstraintViolation<UserDto>> violations = validate(userDto);

    assertEquals(
        Collections.singletonList("phones[0].<list element> -> must match \"phone-[0-9]{4}\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }
}
