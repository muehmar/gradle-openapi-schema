package com.github.muehmar.gradle.openapi.issues.issue159;

import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue159Test {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void personDtoBuilder_when_used_then_genderEnumNotNullable() {
    final PersonDto personDto =
        PersonDto.personDtoBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setGender(PersonDto.GenderEnum.MALE)
            .build();

    assertEquals("John", personDto.getFirstNameOr(""));
    assertEquals("Doe", personDto.getLastNameOr(""));
    assertEquals(PersonDto.GenderEnum.MALE, personDto.getGender());
  }

  @Test
  void deserializePatient_when_validated_then_genderMustNotBeNull() throws JsonProcessingException {
    final String json =
        "{\n"
            + "  \"firstName\": null,\n"
            + "  \"lastName\": null,\n"
            + "  \"gender\": null\n"
            + "}";

    final PersonDto personDto = MAPPER.readValue(json, PersonDto.class);

    final Set<ConstraintViolation<PersonDto>> violations = ValidationUtil.validate(personDto);
    assertEquals(
        Collections.singletonList("gender -> must not be null"), formatViolations(violations));
    assertFalse(personDto.isValid());
  }

  @Test
  void serialize_when_patientDto_then_dataIsNullable() throws JsonProcessingException {
    final PatientDto patientDto =
        PatientDto.patientDtoBuilder().setName("John").setAge(25).setData(Optional.empty()).build();

    assertEquals(
        "{\"age\":25,\"data\":null,\"name\":\"John\"}", MAPPER.writeValueAsString(patientDto));
  }

  @Test
  void deserialize_when_patientDto_then_nullForDataIsValid() throws JsonProcessingException {
    final String json = "{\"age\":25,\"data\":null,\"name\":\"John\"}";

    final PatientDto patientDto = MAPPER.readValue(json, PatientDto.class);

    final Set<ConstraintViolation<PatientDto>> violations = ValidationUtil.validate(patientDto);
    assertEquals(Collections.emptyList(), formatViolations(violations));
    assertTrue(patientDto.isValid());
  }
}
