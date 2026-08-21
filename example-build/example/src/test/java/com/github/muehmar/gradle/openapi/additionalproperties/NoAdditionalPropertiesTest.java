package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static java.lang.reflect.Modifier.isPrivate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class NoAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_noAdditionalProperties_then_noViolations() throws Exception {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"name\":\"hello\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalProperty_then_violation() throws Exception {
    final NoAdditionalPropertiesDto dto =
        MAPPER.readValue("{\"name\":\"hello\",\"color\":\"red\"}", NoAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<NoAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "No additional properties allowed", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void
      getDeclaredMethods_when_normalBuilderClass_then_additionalPropertiesSetterNotPresentOrPrivate() {
    final Method[] methods =
        NoAdditionalPropertiesDto.builder()
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
        NoAdditionalPropertiesDto.builder()
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
