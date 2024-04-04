package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NullableStringPropertiesDto.nullableStringPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class NullableStringPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_dto_then_correctJson() throws JsonProcessingException {
    final NullableStringPropertiesDto dto =
        nullableStringPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("allegra", Tristate.ofValue("svizra"))
            .addAdditionalProperty("hi", Tristate.ofNull())
            .addAdditionalProperty("ciao", Tristate.ofAbsent())
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"foo\":\"foo\",\"hi\":null,\"hello\":\"world\",\"allegra\":\"svizra\"}", json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":\"world\"}";

    final NullableStringPropertiesDto dto =
        MAPPER.readValue(json, NullableStringPropertiesDto.class);

    final NullableStringPropertiesDto expectedDto =
        nullableStringPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("hi", Tristate.ofNull())
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("hi"));
    assertEquals(Tristate.ofValue("world"), dto.getAdditionalProperty("hello"));
    assertEquals(Tristate.ofAbsent(), dto.getAdditionalProperty("notPresent"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(NullableAdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue().orElse(null)))
            .collect(Collectors.joining(", "));

    assertEquals("hello: world, hi: null", joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":\"world\"}";

    final NullableStringPropertiesDto dto =
        MAPPER.readValue(json, NullableStringPropertiesDto.class);

    final Set<ConstraintViolation<NullableStringPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertyNotStringType_then_violation()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    final NullableStringPropertiesDto.Builder builder =
        nullableStringPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Method addAdditionalProperty =
        builder.getClass().getDeclaredMethod("addAdditionalProperty", String.class, Object.class);
    addAdditionalProperty.setAccessible(true);
    addAdditionalProperty.invoke(builder, "hello", 1);

    final NullableStringPropertiesDto dto = builder.build();

    final Set<ConstraintViolation<NullableStringPropertiesDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList(
            "allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of String"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void getAdditionalProperties_when_additionalPropertyNotStringType_then_listIsEmpty()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    final NullableStringPropertiesDto.Builder builder =
        nullableStringPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Method addAdditionalProperty =
        builder.getClass().getDeclaredMethod("addAdditionalProperty", String.class, Object.class);
    addAdditionalProperty.setAccessible(true);
    addAdditionalProperty.invoke(builder, "hello", 1);

    final NullableStringPropertiesDto dto = builder.build();

    final List<NullableAdditionalProperty<String>> additionalProperties =
        dto.getAdditionalProperties();

    assertEquals(Collections.emptyList(), additionalProperties);
  }
}
