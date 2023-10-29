package com.github.muehmar.gradle.openapi.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class CustomTypeObjectValidationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void validate_when_namePropertyIsTooLong_then_doesNotThrowButAlsoValid()
      throws JsonProcessingException {
    final CustomTypeObjectDto dto =
        MAPPER.readValue("{\"name\":\"NameWhichIsActuallyTooLong\"}", CustomTypeObjectDto.class);

    final Set<ConstraintViolation<CustomTypeObjectDto>> violations =
        assertDoesNotThrow(() -> ValidationUtil.validate(dto));

    assertEquals(0, violations.size());
    assertEquals(new Name("NameWhichIsActuallyTooLong"), dto.getName());
  }
}
