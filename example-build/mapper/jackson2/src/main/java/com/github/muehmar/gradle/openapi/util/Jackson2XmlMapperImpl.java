package com.github.muehmar.gradle.openapi.util;

import com.fasterxml.jackson.databind.MapperFeature;

/**
 * Jackson 2 implementation of the XmlMapper interface for XML serialization. This class contains
 * all Jackson 2 XmlMapper specific configuration and imports.
 */
public class Jackson2XmlMapperImpl implements com.github.muehmar.gradle.openapi.util.XmlMapper {

  private final com.fasterxml.jackson.dataformat.xml.XmlMapper xmlMapper;

  public Jackson2XmlMapperImpl() {
    this.xmlMapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();
    configureMapper();
  }

  private void configureMapper() {
    xmlMapper.setConfig(
        xmlMapper.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
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
