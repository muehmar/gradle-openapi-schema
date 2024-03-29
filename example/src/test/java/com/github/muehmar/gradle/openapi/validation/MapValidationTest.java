package com.github.muehmar.gradle.openapi.validation;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class MapValidationTest {
  @Test
  void validate_when_stringMapWithCorrectValue_then_noViolations() {
    final HashMap<String, String> map = new HashMap<>();
    map.put("Hello", "World");
    final StringMapDto dto = StringMapDto.fromProperties(map);

    final Set<ConstraintViolation<StringMapDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_stringMapWithValueNotMatchingThePattern_then_violation() {
    final HashMap<String, String> map = new HashMap<>();
    map.put("Hello", "World!");
    final StringMapDto dto = StringMapDto.fromProperties(map);

    final Set<ConstraintViolation<StringMapDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals("must match \"[A-Za-z]*\"", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_objectMapWithCorrectValue_then_noViolations() {
    final HashMap<String, AllValueObjectDto> map = new HashMap<>();
    map.put("Hello", AllValueObjectDto.builder().build());
    final ObjectMapDto dto = ObjectMapDto.fromProperties(map);

    final Set<ConstraintViolation<ObjectMapDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_objectMapWithInvalidValue_then_violations() {
    final HashMap<String, AllValueObjectDto> map = new HashMap<>();
    map.put("Hello", AllValueObjectDto.builder().andOptionals().setIntValue(35).build());
    final ObjectMapDto dto = ObjectMapDto.fromProperties(map);

    final Set<ConstraintViolation<ObjectMapDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "must be less than or equal to 22", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }
}
