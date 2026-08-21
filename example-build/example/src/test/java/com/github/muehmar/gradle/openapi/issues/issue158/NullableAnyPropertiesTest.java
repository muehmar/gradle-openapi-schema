package com.github.muehmar.gradle.openapi.issues.issue158;

import static com.github.muehmar.gradle.openapi.issues.issue158.NullableAnyPropertiesDto.nullableAnyPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class NullableAnyPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    final NullableAnyPropertiesDto dto =
        nullableAnyPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("ciao", 5)
            .addAdditionalProperty("hi", Tristate.ofNull())
            .addAdditionalProperty("salut", Tristate.ofAbsent())
            .addAdditionalProperty("allegra", Tristate.ofValue(Collections.singletonList(true)))
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"foo\":\"foo\",\"hi\":null,\"ciao\":5,\"hello\":\"world\",\"allegra\":[true]}", json);
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final String json =
        "{\"foo\":\"foo\",\"hi\":null,\"ciao\":5,\"hello\":\"world\",\"allegra\":[true]}";

    final NullableAnyPropertiesDto dto = MAPPER.readValue(json, NullableAnyPropertiesDto.class);

    final NullableAnyPropertiesDto expectedDto =
        nullableAnyPropertiesDtoBuilder()
            .andAllOptionals()
            .setFoo("foo")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("ciao", 5)
            .addAdditionalProperty("hi", Tristate.ofNull())
            .addAdditionalProperty("allegra", Tristate.ofValue(Collections.singletonList(true)))
            .build();

    assertEquals(expectedDto, dto);
    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("hi"));
    assertEquals(Tristate.ofValue("world"), dto.getAdditionalProperty("hello"));
    assertEquals(Tristate.ofValue(5), dto.getAdditionalProperty("ciao"));
    assertEquals(
        Tristate.ofValue(Collections.singletonList(true)), dto.getAdditionalProperty("allegra"));
    assertEquals(Tristate.ofAbsent(), dto.getAdditionalProperty("salut"));
    final String joinedProperties =
        dto.getAdditionalProperties().stream()
            .sorted(Comparator.comparing(NullableAdditionalProperty::getName))
            .map(prop -> String.format("%s: %s", prop.getName(), prop.getValue().orElse(null)))
            .collect(Collectors.joining(", "));

    assertEquals("allegra: [true], ciao: 5, hello: world, hi: null", joinedProperties);
  }

  @Test
  void validate_when_validJson_then_noViolations() throws Exception {
    final String json =
        "{\"foo\":\"foo\",\"hi\":null,\"ciao\":5,\"hello\":\"world\",\"allegra\":[true]}";

    final NullableAnyPropertiesDto dto = MAPPER.readValue(json, NullableAnyPropertiesDto.class);

    final Set<ConstraintViolation<NullableAnyPropertiesDto>> violations = validate(dto);

    assertEquals(Collections.emptySet(), violations);
    assertTrue(dto.isValid());
  }
}
