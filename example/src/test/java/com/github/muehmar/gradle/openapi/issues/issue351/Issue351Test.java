package com.github.muehmar.gradle.openapi.issues.issue351;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class Issue351Test {
  @Test
  void person1DtoBuilder_when_used_then_genderIsTristate() {
    final Person1Dto personDto =
        Person1Dto.fullBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setGender(Tristate.ofValue(Person1Dto.GenderEnum.MALE))
            .build();

    assertEquals(Tristate.ofValue(Person1Dto.GenderEnum.MALE), personDto.getGenderTristate());
  }

  @Test
  void person2DtoBuilder_when_used_then_genderIsTristate() {
    final Person2Dto person2Dto =
        Person2Dto.fullBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .setGender(Tristate.ofValue(GenderDto.MALE))
            .build();

    assertEquals(Tristate.ofValue(GenderDto.MALE), person2Dto.getGenderTristate());
  }
}
