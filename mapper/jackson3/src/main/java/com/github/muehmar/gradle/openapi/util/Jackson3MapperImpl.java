package com.github.muehmar.gradle.openapi.util;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 implementation of the JsonMapper interface. This class contains all Jackson 3 specific
 * configuration and imports.
 */
public class Jackson3MapperImpl implements com.github.muehmar.gradle.openapi.util.JsonMapper {

  private final JsonMapper mapper;

  public Jackson3MapperImpl() {
    // Jackson 3 uses a builder pattern and is immutable
    // JSR-310 (Java 8 Date/Time) support is built-in to Jackson 3
    this.mapper = JsonMapper.builder()
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .build();
  }

  @Override
  public String writeValueAsString(Object value) throws Exception {
    return mapper.writeValueAsString(value);
  }

  @Override
  public <T> T readValue(String content, Class<T> valueType) throws Exception {
    return mapper.readValue(content, valueType);
  }
}
