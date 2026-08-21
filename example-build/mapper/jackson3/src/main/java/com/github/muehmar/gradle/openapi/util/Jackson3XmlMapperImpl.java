package com.github.muehmar.gradle.openapi.util;

import tools.jackson.databind.MapperFeature;
import tools.jackson.dataformat.xml.XmlMapper;

/**
 * Jackson 3 implementation of the XmlMapper interface for XML serialization. This class contains
 * all Jackson 3 XmlMapper specific configuration and imports.
 */
public class Jackson3XmlMapperImpl implements com.github.muehmar.gradle.openapi.util.XmlMapper {

  private final XmlMapper xmlMapper;

  public Jackson3XmlMapperImpl() {
    // Jackson 3 uses a builder pattern and is immutable
    this.xmlMapper = XmlMapper.builder()
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .build();
  }

  @Override
  public String writeValueAsString(Object value) throws Exception {
    return xmlMapper.writeValueAsString(value);
  }

  @Override
  public <T> T readValue(String content, Class<T> valueType) throws Exception {
    return xmlMapper.readValue(content, valueType);
  }
}
