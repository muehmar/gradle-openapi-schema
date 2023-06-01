package com.github.muehmar.gradle.openapi.addprop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class UserHobbiesDtoTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void deserialize() throws JsonProcessingException {
    UserHobbiesDto dto =
        MAPPER.readValue(
            "{\"name\":\"name\",\"description\":\"description\",\"hello\":\"world\"}",
            UserHobbiesDto.class);
    assertEquals("world", dto.getAdditionalProperties().get("hello"));
    assertEquals(Optional.of("world"), dto.getAdditionalProperty("hello"));
    assertEquals("name", dto.getName());
    assertEquals(Optional.of("description"), dto.getDescriptionOpt());
  }

  @Test
  void serialize() throws JsonProcessingException {
    UserHobbiesDto build =
        UserHobbiesDto.newBuilder()
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("hello", "world")
            .build();
    assertEquals("{\"name\":\"name\",\"hello\":\"world\"}", MAPPER.writeValueAsString(build));
  }

  @Test
  void validate1() {
    UserHobbiesDto dto = UserHobbiesDto.newBuilder().setName("name").andOptionals().build();
    final Set<ConstraintViolation<UserHobbiesDto>> violations = ValidationUtil.validate(dto);
    assertEquals(1, violations.size());
  }

  @Test
  void validate2() {
    UserHobbiesDto dto =
        UserHobbiesDto.newBuilder()
            .setName("name")
            .andAllOptionals()
            .setDescription("description")
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("hello2", "world2")
            .build();
    final Set<ConstraintViolation<UserHobbiesDto>> violations = ValidationUtil.validate(dto);
    assertEquals(1, violations.size());
  }

  @Test
  void validate3() {
    UserHobbiesDto dto =
        UserHobbiesDto.newBuilder()
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("hello", "world")
            .build();
    final Set<ConstraintViolation<UserHobbiesDto>> violations = ValidationUtil.validate(dto);
    assertEquals(0, violations.size());
  }
}
