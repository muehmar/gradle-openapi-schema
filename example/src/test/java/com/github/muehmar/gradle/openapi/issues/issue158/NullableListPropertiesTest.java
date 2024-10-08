package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NullableListPropertiesDto.nullableListPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue158.NullableListPropertiesPropertyDto.fromItems;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class NullableListPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_dto_then_correctJson() throws JsonProcessingException {
    final NullableListPropertiesDto dto =
        nullableListPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", fromItems(singletonList("world")))
            .addAdditionalProperty("allegra", Tristate.ofValue(fromItems(singletonList("svizra"))))
            .addAdditionalProperty("hi", Tristate.ofNull())
            .addAdditionalProperty("ciao", Tristate.ofAbsent())
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"foo\":\"foo\",\"hi\":null,\"hello\":[\"world\"],\"allegra\":[\"svizra\"]}", json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":[\"world\"]}";

    final NullableListPropertiesDto dto = MAPPER.readValue(json, NullableListPropertiesDto.class);

    final NullableListPropertiesDto expectedDto =
        nullableListPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", fromItems(singletonList("world")))
            .addAdditionalProperty("hi", Tristate.ofNull())
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("hi"));
    assertEquals(
        Tristate.ofValue(fromItems(singletonList("world"))), dto.getAdditionalProperty("hello"));
    assertEquals(Tristate.ofAbsent(), dto.getAdditionalProperty("ciao"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(NullableAdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue().orElse(null)))
            .collect(Collectors.joining(", "));

    assertEquals(
        "hello: NullableListPropertiesPropertyDto{items=[world]}, hi: null", joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hi\":null,\"hello\":[\"world\"]}";

    final NullableListPropertiesDto dto = MAPPER.readValue(json, NullableListPropertiesDto.class);

    final Set<ConstraintViolation<NullableListPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_additionalPropertyNotListType_then_violation()
      throws IllegalAccessException, NoSuchFieldException {

    final NullableListPropertiesDto.Builder builder =
        nullableListPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Field additionalPropertiesField =
        builder.getClass().getDeclaredField("additionalProperties");
    additionalPropertiesField.setAccessible(true);
    final Map<String, Object> props = (Map<String, Object>) additionalPropertiesField.get(builder);
    props.put("hello", 1);

    final NullableListPropertiesDto dto = builder.build();

    final Set<ConstraintViolation<NullableListPropertiesDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList(
            "allAdditionalPropertiesHaveCorrectType -> Not all additional properties are instances of NullableListPropertiesPropertyDto"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void getAdditionalProperties_when_additionalPropertyNotStringType_then_listIsEmpty()
      throws IllegalAccessException, NoSuchFieldException {

    final NullableListPropertiesDto.Builder builder =
        nullableListPropertiesDtoBuilder().andOptionals().setFoo("foo");

    final Field additionalPropertiesField =
        builder.getClass().getDeclaredField("additionalProperties");
    additionalPropertiesField.setAccessible(true);
    final Map<String, Object> props = (Map<String, Object>) additionalPropertiesField.get(builder);
    props.put("hello", 1);

    final NullableListPropertiesDto dto = builder.build();

    final List<NullableAdditionalProperty<NullableListPropertiesPropertyDto>> additionalProperties =
        dto.getAdditionalProperties();

    assertEquals(Collections.emptyList(), additionalProperties);
  }

  @Test
  void validate_when_listItemStringIsTooLong_then_violation() throws JsonProcessingException {
    final String json = "{\"foo\":\"foo\",\"hello\":[\"worldworldworld\"]}";

    final NullableListPropertiesDto dto = MAPPER.readValue(json, NullableListPropertiesDto.class);

    final Set<ConstraintViolation<NullableListPropertiesDto>> violations = validate(dto);
    assertEquals(
        Collections.singletonList(
            "additionalProperties_[hello].items[0].<list element> -> size must be between 0 and 10"),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }
}
