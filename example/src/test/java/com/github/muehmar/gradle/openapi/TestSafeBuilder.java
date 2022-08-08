package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.v1.model.GenderDto;
import OpenApiSchema.example.api.v1.model.UserDto;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

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

    assertEquals(Optional.empty(), userDto.getBirthdayOpt());
    assertEquals(Optional.of(GenderDto.FEMALE), userDto.getGenderOpt());
    assertEquals(Optional.of(UserDto.RoleEnum.USER), userDto.getRoleOpt());
  }

  @Test
  void andOptionals_when_useEmptyAndNonEmptyOptionals_then_allValuesCorrectSet() {
    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123L)
            .setUser("user")
            .setCity("city")
            .andOptionals()
            .setLocationId("locationId")
            .setBirthday(Optional.empty())
            .setGender(Optional.of(GenderDto.FEMALE))
            .setRole(UserDto.RoleEnum.USER)
            .build();

    assertEquals(Optional.empty(), userDto.getBirthdayOpt());
    assertEquals(Optional.of(GenderDto.FEMALE), userDto.getGenderOpt());
    assertEquals(Optional.of(UserDto.RoleEnum.USER), userDto.getRoleOpt());
    assertEquals(Optional.empty(), userDto.getAgeOpt());
  }
}
