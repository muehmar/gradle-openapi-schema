package com.github.muehmar.gradle.openapi.issues.issue248;

import static com.github.muehmar.gradle.openapi.issues.issue248.StudentDto.studentDtoBuilder;
import static com.github.muehmar.gradle.openapi.issues.issue248.UniversityDto.universityDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue248Test {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fullPersonDtoBuilder_when_used_then_firstNameIsTristate() {
    final PersonDto personDto =
        PersonDto.fullPersonDtoBuilder()
            .setFirstName(Tristate.ofValue("John"))
            .setLastName(Tristate.ofValue("Doe"))
            .build();

    assertEquals(Tristate.ofValue("John"), personDto.getFirstNameTristate());
    assertEquals(Tristate.ofValue("Doe"), personDto.getLastNameTristate());
  }

  @Test
  void serialize_when_studentDto_then_teacherAndSubjectAreNullable()
      throws JsonProcessingException {
    final StudentDto studentDto =
        studentDtoBuilder()
            .setTeacher(Optional.empty())
            .setSubject(Optional.empty())
            .setGrade(6)
            .build();

    assertEquals(Optional.empty(), studentDto.getTeacherOpt());
    assertEquals(Optional.empty(), studentDto.getSubjectOpt());
    assertEquals(
        "{\"grade\":6,\"subject\":null,\"teacher\":null}", MAPPER.writeValueAsString(studentDto));
  }

  @Test
  void deserialize_when_studentDto_then_nullForTeacherAndSubjectIsValid()
      throws JsonProcessingException {
    final String json = "{\"grade\":6,\"teacher\":null,\"subject\":null}";

    final StudentDto studentDto = MAPPER.readValue(json, StudentDto.class);

    assertEquals(Optional.empty(), studentDto.getTeacherOpt());
    assertEquals(Optional.empty(), studentDto.getSubjectOpt());

    final Set<ConstraintViolation<StudentDto>> violations = validate(studentDto);
    assertEquals(Collections.emptyList(), formatViolations(violations));
    assertTrue(studentDto.isValid());
  }

  @Test
  void serialize_when_universityDto_then_subjectsIsNullable() throws JsonProcessingException {
    final UniversityDto universityDto =
        universityDtoBuilder().setName("University").setSubjects(Optional.empty()).build();

    assertEquals(Optional.empty(), universityDto.getSubjectsOpt());
    assertEquals(
        "{\"name\":\"University\",\"subjects\":null}", MAPPER.writeValueAsString(universityDto));
  }

  @Test
  void deserialize_when_universityDto_then_nullForTeacherAndSubjectIsValid()
      throws JsonProcessingException {
    final String json = "{\"name\":\"University\",\"subjects\":null}";

    final UniversityDto universityDto = MAPPER.readValue(json, UniversityDto.class);

    assertEquals(Optional.empty(), universityDto.getSubjectsOpt());

    final Set<ConstraintViolation<UniversityDto>> violations = validate(universityDto);
    assertEquals(Collections.emptyList(), formatViolations(violations));
    assertTrue(universityDto.isValid());
  }
}
