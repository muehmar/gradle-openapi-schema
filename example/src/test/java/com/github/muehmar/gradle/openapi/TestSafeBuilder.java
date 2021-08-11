package com.github.muehmar.gradle.openapi;

import OpenApiSchema.example.api.model.GenderDto;
import OpenApiSchema.example.api.model.UserDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSafeBuilder {
  @Test
  void andAllOptionals_when_useEmptyAndNonEmptyOptionals_then_allValuesCorrectSet() {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123L)
            .setUser("user")
            .setCity("city")
            .andAllOptionals()
            .setLocationId("locationId")
            .setBirthday(Optional.empty())
            .setGender(Optional.of(GenderDto.FEMALE))
            .setAge(2)
            .setLastLogin(LocalDateTime.now())
            .setRole(UserDto.RoleEnum.USER)
            .setCurrencies(Optional.empty())
            .setInterests(Optional.empty())
            .setLanguages(Optional.empty())
            .setHobbies(Optional.empty())
            .setData(Optional.empty())
            .build();

    assertEquals(Optional.empty(), userDto.getBirthdayOptional());
    assertEquals(Optional.of(GenderDto.FEMALE), userDto.getGenderOptional());
    assertEquals(Optional.of(UserDto.RoleEnum.USER), userDto.getRoleOptional());
  }
}
