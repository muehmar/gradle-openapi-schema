package com.github.muehmar.gradle.openapi.additionalproperties;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class ObjectAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static final ObjectAdditionalPropertiesDto DTO;
  private static final String JSON =
      "{\"name\":\"Dexter\",\"prop1\":{\"description\":\"The description\",\"title\":\"Hello\",\"something\":\"more\"}}";

  static {
    final ObjectAdditionalPropertiesPropertyDto additionalProperty =
        ObjectAdditionalPropertiesPropertyDto.builder()
            .setTitle("Hello")
            .andOptionals()
            .setDescription("The description")
            .addAdditionalProperty("something", "more")
            .build();
    DTO =
        ObjectAdditionalPropertiesDto.builder()
            .setName("Dexter")
            .andOptionals()
            .addAdditionalProperty("prop1", additionalProperty)
            .build();
  }

  @Test
  void serialize_when_objectWithObjectAdditionalProperty_then_correctJson() throws Exception {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void deserialize_when_objectWithObjectAdditionalProperty_then_correctDto() throws Exception {
    final ObjectAdditionalPropertiesDto actual =
        MAPPER.readValue(JSON, ObjectAdditionalPropertiesDto.class);
    assertEquals(DTO, actual);
  }

  @Test
  void validate_when_dto_then_noViolation() {
    final Set<ConstraintViolation<ObjectAdditionalPropertiesDto>> violations = validate(DTO);
    assertEquals(0, violations.size());
    assertTrue(DTO.isValid());
  }

  @Test
  void validate_when_descriptionInAdditionalPropertiesObjectTooShort_then_violation() {
    final ObjectAdditionalPropertiesPropertyDto additionalProperty =
        ObjectAdditionalPropertiesPropertyDto.builder()
            .setTitle("Hello")
            .andOptionals()
            .setDescription("d")
            .build();
    final ObjectAdditionalPropertiesDto dto =
        ObjectAdditionalPropertiesDto.builder()
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
        "additionalProperties_[prop1].description",
        violations.stream().findFirst().get().getPropertyPath().toString());
  }
}
