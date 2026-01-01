package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NonNullableStringPropertiesDto.nonNullableStringPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.AdditionalProperty;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class NonNullableStringPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    final NonNullableStringPropertiesDto dto =
        nonNullableStringPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("hi", Optional.empty())
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"foo\":\"foo\",\"hello\":\"world\"}", json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":\"world\"}";

    final NonNullableStringPropertiesDto dto =
        MAPPER.readValue(json, NonNullableStringPropertiesDto.class);

    final NonNullableStringPropertiesDto expectedDto =
        nonNullableStringPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("hi", Optional.empty())
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Optional.empty(), dto.getAdditionalProperty("hi"));
    assertEquals(Optional.of("world"), dto.getAdditionalProperty("hello"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(AdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue()))
            .collect(Collectors.joining(", "));

    assertEquals("hello: world", joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":\"world\"}";

    final NonNullableStringPropertiesDto dto =
        MAPPER.readValue(json, NonNullableStringPropertiesDto.class);

    final Set<ConstraintViolation<NonNullableStringPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertyIsNull_then_violation() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":\"world\"}";

    final NonNullableStringPropertiesDto dto =
        MAPPER.readValue(json, NonNullableStringPropertiesDto.class);

    final Set<ConstraintViolation<NonNullableStringPropertiesDto>> violations = validate(dto);
    assertEquals(
        Collections.singletonList("additionalProperties_[hi].<map value> -> must not be null"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }
}
