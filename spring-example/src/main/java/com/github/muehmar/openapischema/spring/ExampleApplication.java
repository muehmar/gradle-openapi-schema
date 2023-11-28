package com.github.muehmar.openapischema.spring;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class ExampleApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExampleApplication.class, args);
  }

  @Configuration
  public static class AppConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(
          new JavaTimeModule()
              .addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
              .addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE))
              .addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME)));
      objectMapper.setConfig(
          objectMapper.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
      return objectMapper;
    }
  }
}
