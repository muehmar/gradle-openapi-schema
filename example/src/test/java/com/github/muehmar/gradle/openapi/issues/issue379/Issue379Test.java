package com.github.muehmar.gradle.openapi.issues.issue379;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * For a oneOf composition without discriminator, the generated {@code foldOneOf} variant with an
 * {@code onInvalid} supplier documents that the supplier "gets called in case this instance is not
 * valid against exactly one of the defined oneOf schemas". An instance valid against BOTH schemas
 * is invalid for oneOf, but the generated implementation only checks {@code
 * isValidAgainstAdminDto()} first and folds to the first matching schema instead of calling {@code
 * onInvalid}.
 */
public class Issue379Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void foldOneOf_when_jsonValidAgainstBothOneOfSchemas_then_onInvalidCalled() throws Exception {
    final String json = "{\"adminname\":\"admin\",\"username\":\"user\"}";
    final AdminOrUserDto dto = MAPPER.readValue(json, AdminOrUserDto.class);

    final String folded = dto.foldOneOf(admin -> "admin", user -> "user", () -> "invalid");

    assertEquals("invalid", folded);
  }
}
