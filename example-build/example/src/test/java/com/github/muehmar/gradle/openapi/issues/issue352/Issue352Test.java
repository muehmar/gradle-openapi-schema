package com.github.muehmar.gradle.openapi.issues.issue352;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class Issue352Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fullBuilder_when_used_then_genderIsTristate() {
    final PersonDto personDto =
        PersonDto.fullBuilder()
            .setFirstName("John")
            .setLastName("Smith")
            .setGender(Tristate.ofValue(PersonDto.GenderEnum.MALE))
            .build();

    assertEquals(Tristate.ofValue(PersonDto.GenderEnum.MALE), personDto.getGenderTristate());
  }

  @Test
  void validate_when_nullableEnumValueOutOfRange_then_violationWithPlainPropertyPath()
      throws Exception {
    // Issue 266: an out-of-range value of a nullable enum deserializes without throwing and is
    // reported as a violation. The violation path is the plain property name (no Raw suffix),
    // as the api getter of a nullable property carries the Tristate suffix instead.
    final PersonDto personDto =
        MAPPER.readValue(
            "{\"firstName\":\"John\",\"lastName\":\"Smith\",\"gender\":\"diverse\"}",
            PersonDto.class);

    final Set<ConstraintViolation<PersonDto>> violations = validate(personDto);

    assertEquals(
        Collections.singletonList("gender -> must match \"male|female|other|unknown\""),
        formatViolations(violations));
  }
}
