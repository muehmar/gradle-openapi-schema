package com.github.muehmar.gradle.openapi.v2;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestPropertyCountValidation {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  public static Stream<Arguments> patientJsonInputs() {
    return Stream.of(
        arguments("{\"id\":\"123\",\"name\":\"dexter\"}", 0),
        arguments("{\"id\":\"123\",\"name\":null}", 0),
        arguments("{\"id\":\"123\",\"name\":\"dexter\",\"surname\":\"morgan\"}", 0),
        arguments("{\"id\":\"123\",\"name\":\"dexter\",\"age\":40}", 0),
        arguments("{\"id\":\"123\",\"name\":\"dexter\",\"age\":null}", 0),
        arguments("{\"id\":\"123\",\"name\":\"dexter\",\"surname\":\"morgan\",\"age\":40}", 1),
        arguments("{\"id\":\"123\",\"name\":\"dexter\",\"surname\":\"morgan\",\"age\":null}", 1),
        arguments("{\"id\":\"123\"}", 2),
        arguments("{}", 3),
        arguments("{\"id\":\"123\",\"surname\":\"morgan\"}", 1));
  }

  @ParameterizedTest
  @MethodSource("patientJsonInputs")
  void validate_when_jsonInput_then_matchExpectedValidationViolationCount(
      String inputJson, int expectedViolationsCount) throws Exception {
    final PatientDto dto = MAPPER.readValue(inputJson, PatientDto.class);

    final Set<ConstraintViolation<PatientDto>> violations = validate(dto);

    assertEquals(expectedViolationsCount, violations.size());
    assertEquals(dto.isValid(), expectedViolationsCount == 0);
  }
}
