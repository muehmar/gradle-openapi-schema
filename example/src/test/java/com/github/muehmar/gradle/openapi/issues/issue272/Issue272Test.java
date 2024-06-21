package com.github.muehmar.gradle.openapi.issues.issue272;

import static com.github.muehmar.gradle.openapi.issues.issue272.RoleDto.fullRoleDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class Issue272Test {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_uppercaseProperty_then_serializedCorrectly() throws JsonProcessingException {
    final RoleDto roleDto = fullRoleDtoBuilder().setTC("value").build();

    final String json = MAPPER.writeValueAsString(roleDto);
    assertEquals("{\"TC\":\"value\"}", json);
  }
}
