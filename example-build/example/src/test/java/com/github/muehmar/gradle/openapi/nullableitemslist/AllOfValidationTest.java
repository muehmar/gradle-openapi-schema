package com.github.muehmar.gradle.openapi.nullableitemslist;

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

public class AllOfValidationTest {
  private static final JsonMapper OBJECT_MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_validIds_then_noViolations() throws Exception {
    final String json = "{\"ids\":[\"id-1234\"],\"usernames\":null}";
    final SuperUserDto userDto = OBJECT_MAPPER.readValue(json, SuperUserDto.class);

    final Set<ConstraintViolation<SuperUserDto>> violations = validate(userDto);

    assertEquals(0, violations.size());
    assertTrue(userDto.isValid());
  }

  @Test
  void validate_when_invalidLists_then_correctViolationMessages() throws Exception {
    final String json =
        "{\"emails\":[\"email-1234\"],\"ids\":[],\"phones\":[\"phone-1234\"],\"superUserId\":\"super-user-id\",\"usernames\":[\"user-1234\"]}";
    final SuperUserDto userDto = OBJECT_MAPPER.readValue(json, SuperUserDto.class);

    final Set<ConstraintViolation<SuperUserDto>> violations = validate(userDto);

    assertEquals(
        Arrays.asList(
            "userDto.emails -> size must be between 5 and 6",
            "userDto.idsRaw -> size must be between 1 and 2",
            "userDto.phones -> size must be between 7 and 8",
            "userDto.usernames -> size must be between 3 and 4"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(userDto.isValid());
  }
}
