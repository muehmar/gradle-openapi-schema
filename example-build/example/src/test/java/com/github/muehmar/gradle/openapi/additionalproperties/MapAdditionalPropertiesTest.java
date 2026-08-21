package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.AdditionalProperty;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class MapAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_validAdditionalProperty_then_noViolationsAndValueReturned() throws Exception {
    final MapAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"name\",\"data\":{\"hello\":\"world\"}}", MapAdditionalPropertiesDto.class);

    assertEquals(
        Optional.of(Collections.singletonList(new AdditionalProperty<>("hello", "world"))),
        dto.getAdditionalProperty("data")
            .map(MapAdditionalPropertiesPropertyDto::getAdditionalProperties));

    final Set<ConstraintViolation<MapAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void serialize_when_withArrayAsAdditionalProperty_then_correctJson() throws Exception {
    final MapAdditionalPropertiesDto dto =
        MapAdditionalPropertiesDto.builder()
            .setName("name")
            .andAllOptionals()
            .addAdditionalProperty(
                "data",
                new MapAdditionalPropertiesPropertyDto(Collections.singletonMap("hello", "world")))
            .build();

    final String json = MAPPER.writeValueAsString(dto);
    assertEquals("{\"name\":\"name\",\"data\":{\"hello\":\"world\"}}", json);
  }
}
