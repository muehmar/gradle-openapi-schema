package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NonNullableListPropertiesDto.nonNullableListPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue158.NonNullableListPropertiesPropertyDto.fromItems;
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

public class NonNullableListPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    final NonNullableListPropertiesDto dto =
        nonNullableListPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", fromItems(Collections.singletonList("world")))
            .addAdditionalProperty("hi", Optional.empty())
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"foo\":\"foo\",\"hello\":[\"world\"]}", json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":[\"world\"]}";

    final NonNullableListPropertiesDto dto =
        MAPPER.readValue(json, NonNullableListPropertiesDto.class);

    final NonNullableListPropertiesDto expectedDto =
        nonNullableListPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", fromItems(Collections.singletonList("world")))
            .addAdditionalProperty("hi", Optional.empty())
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Optional.empty(), dto.getAdditionalProperty("hi"));
    assertEquals(
        Optional.of(fromItems(Collections.singletonList("world"))),
        dto.getAdditionalProperty("hello"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(AdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue()))
            .collect(Collectors.joining(", "));

    assertEquals("hello: NonNullableListPropertiesPropertyDto{items=[world]}", joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":[\"world\"]}";

    final NonNullableListPropertiesDto dto =
        MAPPER.readValue(json, NonNullableListPropertiesDto.class);

    final Set<ConstraintViolation<NonNullableListPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertyIsNull_then_violation() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":null}";

    final NonNullableListPropertiesDto dto =
        MAPPER.readValue(json, NonNullableListPropertiesDto.class);

    final Set<ConstraintViolation<NonNullableListPropertiesDto>> violations = validate(dto);
    assertEquals(
        Collections.singletonList("additionalProperties_[hello].<map value> -> must not be null"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_listItemStringIsTooLong_then_violation() throws Exception {
    final String json = "{\"foo\":\"foo\",\"hello\":[\"worldworldworld\"]}";

    final NonNullableListPropertiesDto dto =
        MAPPER.readValue(json, NonNullableListPropertiesDto.class);

    final Set<ConstraintViolation<NonNullableListPropertiesDto>> violations = validate(dto);
    assertEquals(
        Collections.singletonList(
            "additionalProperties_[hello].items[0].<list element> -> size must be between 0 and 10"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }
}
