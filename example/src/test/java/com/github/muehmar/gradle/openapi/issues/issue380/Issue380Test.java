package com.github.muehmar.gradle.openapi.issues.issue380;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import org.junit.jupiter.api.Test;

/**
 * The OpenAPI discriminator mapping may map several payload values to the same schema:
 *
 * <pre>
 * mapping:
 *   adm: '#/components/schemas/Admin'
 *   administrator: '#/components/schemas/Admin'
 *   usr: '#/components/schemas/User'
 * </pre>
 *
 * The generator reduces the mapping to a single (arbitrary) key per schema, so payloads using any
 * of the other mapped values ({@code "adm"} here) fail validation and fold although they are valid
 * according to the specification.
 */
public class Issue380Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_discriminatorValueIsFirstMappedAlias_then_noViolations() throws Exception {
    final String json = "{\"type\":\"adm\",\"adminname\":\"admin\"}";
    final AdminOrUserDto dto = MAPPER.readValue(json, AdminOrUserDto.class);

    assertTrue(
        ValidationUtil.validate(dto).isEmpty(),
        "type=adm is mapped to Admin and the payload is valid against Admin");
  }

  @Test
  void foldOneOf_when_discriminatorValueIsFirstMappedAlias_then_foldsToAdmin() throws Exception {
    final String json = "{\"type\":\"adm\",\"adminname\":\"admin\"}";
    final AdminOrUserDto dto = MAPPER.readValue(json, AdminOrUserDto.class);

    final String folded = dto.foldOneOf(admin -> "admin", user -> "user", () -> "invalid");

    assertEquals("admin", folded);
  }

  @Test
  void validate_when_discriminatorValueIsSecondMappedAlias_then_noViolations() throws Exception {
    // Green companion: the alias the generator happened to keep works
    final String json = "{\"type\":\"administrator\",\"adminname\":\"admin\"}";
    final AdminOrUserDto dto = MAPPER.readValue(json, AdminOrUserDto.class);

    assertTrue(ValidationUtil.validate(dto).isEmpty());
  }
}
