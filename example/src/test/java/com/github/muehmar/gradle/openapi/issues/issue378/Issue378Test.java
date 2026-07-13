package com.github.muehmar.gradle.openapi.issues.issue378;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

/**
 * For an anyOf composition it is explicitly allowed that an instance is valid against more than one
 * schema. The generated getters {@code getAdminDto()}/{@code getUserDto()} promise to return the
 * DTO whenever the instance is valid against the corresponding schema. The generated implementation
 * uses {@code .stream().findFirst()} on the foldAnyOf result and therefore always inspects only the
 * first anyOf schema: for an instance valid against both schemas, {@code getUserDto()} wrongly
 * returns an empty Optional.
 */
public class Issue378Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void getDtos_when_jsonValidAgainstBothAnyOfSchemas_then_bothDtosPresent() throws Exception {
    final String json = "{\"adminname\":\"admin\",\"username\":\"user\"}";
    final AdminOrUserDto dto = MAPPER.readValue(json, AdminOrUserDto.class);

    assertTrue(dto.getAdminDto().isPresent(), "Admin schema matches -> AdminDto expected");
    assertTrue(dto.getUserDto().isPresent(), "User schema matches -> UserDto expected");
  }
}
