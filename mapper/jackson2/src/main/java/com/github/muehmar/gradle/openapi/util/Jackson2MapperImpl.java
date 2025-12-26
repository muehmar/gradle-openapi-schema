package com.github.muehmar.gradle.openapi.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 2 implementation of the JsonMapper interface. This class contains all Jackson 2 specific
 * configuration and imports.
 */
public class Jackson2MapperImpl implements JsonMapper {

  private final ObjectMapper mapper;

  public Jackson2MapperImpl() {
    this.mapper = new ObjectMapper();
    configureMapper();
  }

  private void configureMapper() {
    mapper.registerModule(
        new JavaTimeModule()
            .addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
            .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE))
            .addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME)));
    mapper.setConfig(
        mapper.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
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
