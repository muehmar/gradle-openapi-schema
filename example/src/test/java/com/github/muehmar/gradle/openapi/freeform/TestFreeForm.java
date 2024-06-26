package com.github.muehmar.gradle.openapi.freeform;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.HashMap;
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
    final FreeForm1Dto dto1 = FreeForm1Dto.fromProperties(map);
    final FreeForm2Dto dto2 = FreeForm2Dto.fromProperties(map);
    final FreeForm3Dto dto3 = FreeForm3Dto.fromProperties(map);

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
        InlineFreeFormDto.builder().andAllOptionals().setData(map).build();

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

    assertEquals(Tristate.ofValue("Dexter"), dto1.getAdditionalProperty("firstName"));
    assertEquals(Tristate.ofValue("Morgan"), dto1.getAdditionalProperty("lastName"));

    assertEquals(FreeForm1Dto.fromProperties(expectedMap), dto1);
    assertEquals(FreeForm2Dto.fromProperties(expectedMap), dto2);
    assertEquals(FreeForm3Dto.fromProperties(expectedMap), dto3);
  }

  @Test
  void deserialize_when_inlineFreeFormDto_then_correctObject() throws JsonProcessingException {
    final String input = "{\"data\":{\"firstName\":\"Dexter\",\"lastName\":\"Morgan\"}}";

    final InlineFreeFormDto dto = MAPPER.readValue(input, InlineFreeFormDto.class);

    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final InlineFreeFormDto expectedDto =
        InlineFreeFormDto.builder().andAllOptionals().setData(map).build();

    assertEquals(expectedDto, dto);
  }

  @Test
  void getPropertyCount_when_freeFormDtos_then_correctCount() {
    final HashMap<String, Object> map = new HashMap<>();
    map.put("firstName", "Dexter");
    map.put("lastName", "Morgan");
    final FreeForm1Dto dto1 = FreeForm1Dto.fromProperties(map);
    final FreeForm2Dto dto2 = FreeForm2Dto.fromProperties(map);
    final FreeForm3Dto dto3 = FreeForm3Dto.fromProperties(map);

    assertEquals(2, dto1.getPropertyCount());
    assertEquals(2, dto2.getPropertyCount());
    assertEquals(2, dto3.getPropertyCount());
  }

  @ParameterizedTest
  @CsvSource({"0,1", "1,0", "2,0", "3,0", "4,1"})
  void validate_when_freeFormDto1_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm1Dto dto = FreeForm1Dto.fromProperties(createPropertyMap(propertyCount));

    final Set<?> violations = validate(dto);

    assertEquals(violationCount, violations.size());
    assertEquals(violationCount == 0, dto.isValid());
  }

  @ParameterizedTest
  @CsvSource({"1,1", "2,0", "3,0", "4,0", "5,1"})
  void validate_when_freeFormDto2_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm2Dto dto = FreeForm2Dto.fromProperties(createPropertyMap(propertyCount));

    final Set<?> violations = validate(dto);

    assertEquals(violationCount, violations.size());
    assertEquals(violationCount == 0, dto.isValid());
  }

  @ParameterizedTest
  @CsvSource({"2,1", "3,0", "4,0", "5,0", "6,1"})
  void validate_when_freeFormDto3_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final FreeForm3Dto dto = FreeForm3Dto.fromProperties(createPropertyMap(propertyCount));

    final Set<?> violations = validate(dto);

    assertEquals(violationCount, violations.size());
    assertEquals(violationCount == 0, dto.isValid());
  }

  @ParameterizedTest
  @CsvSource({"3,1", "4,0", "5,0", "6,0", "7,1"})
  void validate_when_inlineFreeFormDto_then_matchExpectedViolationCount(
      int propertyCount, int violationCount) {
    final InlineFreeFormDto dto =
        InlineFreeFormDto.builder()
            .andAllOptionals()
            .setData(createPropertyMap(propertyCount))
            .build();

    final Set<?> violations = validate(dto);

    assertEquals(violationCount, violations.size());
    assertEquals(violationCount == 0, dto.isValid());
  }

  private static HashMap<String, Object> createPropertyMap(int propertyCount) {
    final HashMap<String, Object> map = new HashMap<>();
    for (int i = 0; i < propertyCount; i++) {
      map.put("prop" + i, i);
    }
    return map;
  }
}
