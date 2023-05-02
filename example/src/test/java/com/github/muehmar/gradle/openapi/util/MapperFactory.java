package com.github.muehmar.gradle.openapi.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.format.DateTimeFormatter;

public class MapperFactory {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(
        new JavaTimeModule()
            .addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
            .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE))
            .addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME)));
    MAPPER.setConfig(
        MAPPER.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
  }

  private MapperFactory() {}

  public static ObjectMapper mapper() {
    return MAPPER;
  }
}
