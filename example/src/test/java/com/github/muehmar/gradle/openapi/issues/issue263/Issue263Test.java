package com.github.muehmar.gradle.openapi.issues.issue263;

import static com.github.muehmar.gradle.openapi.issues.issue263.Bar1Dto.fullBar1DtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue263.Bar2Dto.fullBar2DtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue263Test {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fullBar1DtoBuilder_when_used_then_correctDto() {
    final Bar1Dto barDto = fullBar1DtoBuilder().setHello("World").setFoo("Bar").build();

    assertEquals("World", barDto.getHello());
    assertEquals(Optional.of("Bar"), barDto.getFooOpt());
  }

  @Test
  void deserializeBar1Json_when_fooIsNull_then_noViolation() throws JsonProcessingException {
    final String json = "{\"hello\":\"World\",\"foo\":null}";

    final Bar1Dto barDto = MAPPER.readValue(json, Bar1Dto.class);

    final Set<ConstraintViolation<Bar1Dto>> violations = validate(barDto);

    assertEquals(0, violations.size());

    assertEquals("World", barDto.getHello());
    assertEquals(Optional.empty(), barDto.getFooOpt());
  }

  @Test
  void fullBar2DtoBuilder_when_used_then_correctDto() {
    final Bar2Dto barDto = fullBar2DtoBuilder().setHello("World").setFoo("Bar").build();

    assertEquals("World", barDto.getHello());
    assertEquals("Bar", barDto.getFoo());
  }

  @Test
  void deserializeBar2Json_when_fooIsNull_then_violation() throws JsonProcessingException {
    final String json = "{\"hello\":\"World\",\"foo\":null}";

    final Bar2Dto barDto = MAPPER.readValue(json, Bar2Dto.class);

    final Set<ConstraintViolation<Bar2Dto>> violations = validate(barDto);

    assertEquals(1, violations.size());

    assertEquals(
        Collections.singletonList("foo -> must not be null"), formatViolations(violations));
  }
}
