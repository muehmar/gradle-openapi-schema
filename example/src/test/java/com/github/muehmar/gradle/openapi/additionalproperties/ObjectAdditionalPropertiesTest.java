package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.additionalproperties.model.ObjectAdditionalPropertiesDto;
import openapischema.example.api.additionalproperties.model.ObjectAdditionalPropertiesPropertyDto;
import org.junit.jupiter.api.Test;

class ObjectAdditionalPropertiesTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  private static final ObjectAdditionalPropertiesDto DTO;
  private static final String JSON =
      "{\"name\":\"Dexter\",\"prop1\":{\"description\":\"The description\",\"title\":\"Hello\",\"something\":\"more\"}}";

  static {
    final ObjectAdditionalPropertiesPropertyDto additionalProperty =
        ObjectAdditionalPropertiesPropertyDto.newBuilder()
            .setTitle("Hello")
            .andOptionals()
            .setDescription("The description")
            .addAdditionalProperty("something", "more")
            .build();
    DTO =
        ObjectAdditionalPropertiesDto.newBuilder()
            .setName("Dexter")
            .andOptionals()
            .addAdditionalProperty("prop1", additionalProperty)
            .build();
  }

  @Test
  void serialize_when_objectWithObjectAdditionalProperty_then_correctJson()
      throws JsonProcessingException {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void deserialize_when_objectWithObjectAdditionalProperty_then_correctDto()
      throws JsonProcessingException {
    assertEquals(DTO, MAPPER.readValue(JSON, ObjectAdditionalPropertiesDto.class));
  }

  @Test
  void validate_when_dto_then_noViolation() {
    final Set<ConstraintViolation<ObjectAdditionalPropertiesDto>> violations = validate(DTO);
    assertEquals(0, violations.size());
  }

  @Test
  void validate_when_descriptionInAdditionalPropertiesObjectTooShort_then_violation() {
    final ObjectAdditionalPropertiesPropertyDto additionalProperty =
        ObjectAdditionalPropertiesPropertyDto.newBuilder()
            .setTitle("Hello")
            .andOptionals()
            .setDescription("d")
            .build();
    final ObjectAdditionalPropertiesDto dto =
        ObjectAdditionalPropertiesDto.newBuilder()
            .setName("Dexter")
            .andOptionals()
            .addAdditionalProperty("prop1", additionalProperty)
            .build();

    final Set<ConstraintViolation<ObjectAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 5 and 2147483647",
        violations.stream().findFirst().get().getMessage());
    assertEquals(
        "additionalProperties[prop1].descriptionRaw",
        violations.stream().findFirst().get().getPropertyPath().toString());
  }
}
