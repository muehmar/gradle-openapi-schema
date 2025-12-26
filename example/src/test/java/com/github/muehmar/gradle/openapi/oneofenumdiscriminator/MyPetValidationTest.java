package com.github.muehmar.gradle.openapi.oneofenumdiscriminator;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class MyPetValidationTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_matchesCatSchema_then_noViolation() throws Exception {
    final MyPetDto myPetDto =
        MAPPER.readValue("{\"id\":\"cat-id\",\"type\":\"Cat\",\"name\":\"mimmi\"}", MyPetDto.class);

    final Set<ConstraintViolation<MyPetDto>> violations = validate(myPetDto);

    assertEquals(0, violations.size());
    assertTrue(myPetDto.isValid());
  }
}
