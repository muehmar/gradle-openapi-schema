package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NullableObjectPropertiesDto.nullableObjectPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue158.NullableObjectPropertiesPropertyDto.nullableObjectPropertiesPropertyDtoBuilder;
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

public class NullableObjectPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_dto_then_correctJson() throws JsonProcessingException {
    final NullableObjectPropertiesDto dto =
        nullableObjectPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty(
                "hello", nullableObjectPropertiesPropertyDtoBuilder().setBar("bar").build())
            .addAdditionalProperty(
                "allegra",
                Tristate.ofValue(
                    nullableObjectPropertiesPropertyDtoBuilder().setBar("svizra").build()))
            .addAdditionalProperty("hi", Tristate.ofNull())
            .addAdditionalProperty("ciao", Tristate.ofAbsent())
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"foo\":\"foo\",\"hi\":null,\"hello\":{\"bar\":\"bar\"},\"allegra\":{\"bar\":\"svizra\"}}",
        json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":{\"bar\":\"bar\"}}";

    final NullableObjectPropertiesDto dto =
        MAPPER.readValue(json, NullableObjectPropertiesDto.class);

    final NullableObjectPropertiesDto expectedDto =
        nullableObjectPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty(
                "hello", nullableObjectPropertiesPropertyDtoBuilder().setBar("bar").build())
            .addAdditionalProperty("hi", Tristate.ofNull())
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("hi"));
    assertEquals(
        Tristate.ofValue(nullableObjectPropertiesPropertyDtoBuilder().setBar("bar").build()),
        dto.getAdditionalProperty("hello"));
    assertEquals(Tristate.ofAbsent(), dto.getAdditionalProperty("notPresent"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(NullableAdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue().orElse(null)))
            .collect(Collectors.joining(", "));

    assertEquals(
        "hello: NullableObjectPropertiesPropertyDto{bar='bar', additionalProperties={}}, hi: null",
        joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":{\"bar\":\"bar\"}}";

    final NullableObjectPropertiesDto dto =
        MAPPER.readValue(json, NullableObjectPropertiesDto.class);

    final Set<ConstraintViolation<NullableObjectPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_stringTooLongForObjectProperty_then_violation()
      throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":{\"bar\":\"barbarbarbar\"}}";

    final NullableObjectPropertiesDto dto =
        MAPPER.readValue(json, NullableObjectPropertiesDto.class);

    final Set<ConstraintViolation<NullableObjectPropertiesDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList(
            "additionalProperties_[hello].bar -> size must be between 0 and 10"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertyNotObjectType_then_violation()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    final NullableObjectPropertiesDto.Builder builder =
        nullableObjectPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Method addAdditionalProperty =
        builder.getClass().getDeclaredMethod("addAdditionalProperty", String.class, Object.class);
    addAdditionalProperty.setAccessible(true);
    addAdditionalProperty.invoke(builder, "hello", 1);

    final NullableObjectPropertiesDto dto = builder.build();

    final Set<ConstraintViolation<NullableObjectPropertiesDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList(
            "allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of NullableObjectPropertiesPropertyDto"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void getAdditionalProperties_when_additionalPropertyNotObjectType_then_listIsEmpty()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    final NullableObjectPropertiesDto.Builder builder =
        nullableObjectPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Method addAdditionalProperty =
        builder.getClass().getDeclaredMethod("addAdditionalProperty", String.class, Object.class);
    addAdditionalProperty.setAccessible(true);
    addAdditionalProperty.invoke(builder, "hello", 1);

    final NullableObjectPropertiesDto dto = builder.build();

    final List<NullableAdditionalProperty<NullableObjectPropertiesPropertyDto>>
        additionalProperties = dto.getAdditionalProperties();

    assertEquals(Collections.emptyList(), additionalProperties);
  }
}
