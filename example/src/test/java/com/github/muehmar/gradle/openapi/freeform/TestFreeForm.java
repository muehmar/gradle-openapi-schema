package com.github.muehmar.gradle.openapi.freeform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.freeform.model.FreeForm1Dto;
import OpenApiSchema.example.api.freeform.model.FreeForm2Dto;
import OpenApiSchema.example.api.freeform.model.FreeForm3Dto;
import OpenApiSchema.example.api.freeform.model.InlineFreeFormDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TestFreeForm {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_rootFreeFormDtos_then_correctJson() throws JsonProcessingException {
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
  void serialize_when_inlineFreeFormDto_then_correctJson() throws JsonProcessingException {
    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final InlineFreeFormDto dto =
        InlineFreeFormDto.newBuilder().andAllOptionals().setData(map).build();

    final String expectedJson = "{\"data\":{\"firstName\":\"Dexter\",\"lastName\":\"Morgan\"}}";
    assertEquals(expectedJson, MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_rootFreeFormDtos_then_correctObject() throws JsonProcessingException {
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
  void deserialize_when_inlineFreeFormDto_then_correctObject() throws JsonProcessingException {
    final String input = "{\"data\":{\"firstName\":\"Dexter\",\"lastName\":\"Morgan\"}}";

    final InlineFreeFormDto dto = MAPPER.readValue(input, InlineFreeFormDto.class);

    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final InlineFreeFormDto expectedDto =
        InlineFreeFormDto.newBuilder().andAllOptionals().setData(map).build();

    assertEquals(expectedDto, dto);
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
  @CsvSource({"0,1", "1,0", "2,0", "3,0", "4,1"})
  void validate_when_freeFormDto1_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm1Dto dto = new FreeForm1Dto(createPropertyMap(propertyCount));

    final Set<?> violations = ValidationUtil.validate(dto);

    assertEquals(violationCount, violations.size());
  }

  @ParameterizedTest
  @CsvSource({"1,1", "2,0", "3,0", "4,0", "5,1"})
  void validate_when_freeFormDto2_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm2Dto dto = new FreeForm2Dto(createPropertyMap(propertyCount));

    final Set<?> violations = ValidationUtil.validate(dto);

    assertEquals(violationCount, violations.size());
  }

  @ParameterizedTest
  @CsvSource({"2,1", "3,0", "4,0", "5,0", "6,1"})
  void validate_when_freeFormDto3_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm3Dto dto = new FreeForm3Dto(createPropertyMap(propertyCount));

    final Set<?> violations = ValidationUtil.validate(dto);

    assertEquals(violationCount, violations.size());
  }

  @ParameterizedTest
  @CsvSource({"3,1", "4,0", "5,0", "6,0", "7,1"})
  void validate_when_inlineFreeFormDto_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final InlineFreeFormDto dto =
        InlineFreeFormDto.newBuilder()
            .andAllOptionals()
            .setData(createPropertyMap(propertyCount))
            .build();

    final Set<?> violations = ValidationUtil.validate(dto);

    assertEquals(violationCount, violations.size());
  }

  private static HashMap<String, Object> createPropertyMap(int propertyCount) {
    final HashMap<String, Object> map = new HashMap<>();
    for (int i = 0; i < propertyCount; i++) {
      map.put("prop" + i, i);
    }
    return map;
  }
}
