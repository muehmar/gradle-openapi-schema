package com.github.muehmar.gradle.openapi.freeform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.freeform.model.FreeForm1Dto;
import OpenApiSchema.example.api.freeform.model.FreeForm2Dto;
import OpenApiSchema.example.api.freeform.model.FreeForm3Dto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestFreeForm {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  static void setupMapper() {
    MAPPER.setConfig(
        MAPPER.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
  }

  @Test
  void serialize_when_freeFormDtos_then_correctJson() throws JsonProcessingException {
    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final FreeForm1Dto dto1 = new FreeForm1Dto(map);
    final FreeForm2Dto dto2 = new FreeForm2Dto(map);
    final FreeForm3Dto dto3 = new FreeForm3Dto(map);

    final String expectedJson = "{\"firstName\":\"Dexter\",\"lastName\":\"Morgan\"}";
    assertEquals(expectedJson, MAPPER.writeValueAsString(dto1));
    assertEquals(expectedJson, MAPPER.writeValueAsString(dto2));
    assertEquals(expectedJson, MAPPER.writeValueAsString(dto3));
  }

  @Test
  void deserialize_when_freeFormDtos_then_correctObject() throws JsonProcessingException {
    final String input = "{\"firstName\":\"Dexter\",\"lastName\":\"Morgan\"}";

    final FreeForm1Dto dto1 = MAPPER.readValue(input, FreeForm1Dto.class);
    final FreeForm2Dto dto2 = MAPPER.readValue(input, FreeForm2Dto.class);
    final FreeForm3Dto dto3 = MAPPER.readValue(input, FreeForm3Dto.class);

    final HashMap<String, Object> expectedMap = new HashMap<>();
    expectedMap.put("firstName", "Dexter");
    expectedMap.put("lastName", "Morgan");

    assertEquals(Optional.of("Dexter"), dto1.getProperty("firstName"));
    assertEquals(Optional.of("Morgan"), dto1.getProperty("lastName"));

    assertEquals(new FreeForm1Dto(expectedMap), dto1);
    assertEquals(new FreeForm2Dto(expectedMap), dto2);
    assertEquals(new FreeForm3Dto(expectedMap), dto3);
  }

  @Test
  void getPropertyCount_when_freeFormDtos_then_correctCount() {
    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final FreeForm1Dto dto1 = new FreeForm1Dto(map);
    final FreeForm2Dto dto2 = new FreeForm2Dto(map);
    final FreeForm3Dto dto3 = new FreeForm3Dto(map);

    assertEquals(2, dto1.getPropertyCount());
    assertEquals(2, dto2.getPropertyCount());
    assertEquals(2, dto3.getPropertyCount());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4})
  void validate_when_freeFormDto1WithTwoProperties_then_valid(int propertyCount) {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      final Validator validator = validatorFactory.getValidator();

      final HashMap<String, Object> map = new HashMap<>();

      for (int i = 0; i < propertyCount; i++) {
        map.put("prop" + i, i);
      }

      final FreeForm1Dto dto1 = new FreeForm1Dto(map);

      final Set<ConstraintViolation<FreeForm1Dto>> violations = validator.validate(dto1);

      if (1 <= propertyCount && propertyCount <= 3) {
        assertEquals(0, violations.size());
      } else {
        assertEquals(1, violations.size());
      }
    }
  }
}
