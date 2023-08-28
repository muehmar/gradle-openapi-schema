package com.github.muehmar.gradle.openapi.additionalproperties;

import static java.lang.reflect.Modifier.isPrivate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import openapischema.example.api.additionalproperties.model.NoAdditionalPropertiesDto;
import org.junit.jupiter.api.Test;

class NoAdditionalPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_noAdditionalProperties_then_noViolations() throws JsonProcessingException {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"name\":\"hello\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> violations =
        ValidationUtil.validate(dto);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_additionalProperty_then_violation() throws JsonProcessingException {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"name\":\"hello\",\"color\":\"red\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> violations =
        ValidationUtil.validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "No additional properties allowed", violations.stream().findFirst().get().getMessage());
  }

  @Test
  void
      getDeclaredMethods_when_normalBuilderClass_then_additionalPropertiesSetterNotPresentOrPrivate() {
    final Method[] methods =
        NoAdditionalPropertiesDto.newBuilder()
            .setName("")
            .andOptionals()
            .getClass()
            .getDeclaredMethods();

    final Optional<Method> jsonAnySetter =
        Stream.of(methods).filter(m -> m.getName().equals("addAdditionalProperty")).findFirst();
    assertTrue(jsonAnySetter.isPresent());
    assertTrue(
        isPrivate(jsonAnySetter.get().getModifiers()),
        "Modifier is " + Modifier.toString(jsonAnySetter.get().getModifiers()));

    for (Method method : methods) {
      assertNotEquals("setAdditionalProperties", method.getName());
    }
  }

  @Test
  void getDeclaredMethods_when_allOptionalsBuilderClass_then_noAdditionalPropertiesSetterFound() {
    final Method[] methods =
        NoAdditionalPropertiesDto.newBuilder()
            .setName("")
            .andAllOptionals()
            .getClass()
            .getDeclaredMethods();
    for (Method method : methods) {
      assertNotEquals("addAdditionalProperty", method.getName());
      assertNotEquals("setAdditionalProperties", method.getName());
    }
  }

  @Test
  void getDeclaredMethods_when_noAdditionalPropertiesDto_then_noAdditionalPropertiesGetterFound() {
    final Method[] methods = NoAdditionalPropertiesDto.class.getDeclaredMethods();
    for (Method method : methods) {
      assertNotEquals("getAdditionalProperty", method.getName());
      assertNotEquals("getAdditionalProperties", method.getName());
    }
  }
}
