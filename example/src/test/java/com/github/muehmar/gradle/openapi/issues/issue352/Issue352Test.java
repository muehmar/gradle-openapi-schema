package com.github.muehmar.gradle.openapi.issues.issue352;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class Issue352Test {
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
}
