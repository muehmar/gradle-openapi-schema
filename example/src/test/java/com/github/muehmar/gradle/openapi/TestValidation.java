package com.github.muehmar.gradle.openapi;

import OpenApiSchema.example.api.model.UserDto;
import OpenApiSchema.example.api.model.UserGroupDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestValidation {

  @Test
  void validate_when_validUserGroupDtoAndUserDto_then_noConstraintViolation() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(5L)
            .setUser("Username")
            .setCity("Winti")
            .build();

    final ArrayList<UserDto> members = new ArrayList<>();
    members.add(userDto);

    final UserGroupDto userGroupDto =
        UserGroupDto.newBuilder()
            .andOptionals()
            .setLanguages(new ArrayList<>())
            .setMembers(members)
            .setOwner(userDto)
            .build();

    final Set<ConstraintViolation<UserGroupDto>> constraintViolations =
        validator.validate(userGroupDto);

    assertTrue(constraintViolations.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(ints = {5, 6, 7, 8, 23, 24, 25, 26})
  void validate_when_differentUsernameLength_then_noConstraintViolationForCorrectLength(
      int length) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final String username = String.join("", Collections.nCopies(length, "A"));

    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(5L)
            .setUser(username)
            .setCity("Winti")
            .build();

    final ArrayList<UserDto> members = new ArrayList<>();
    members.add(userDto);

    final UserGroupDto userGroupDto =
        UserGroupDto.newBuilder()
            .andOptionals()
            .setLanguages(new ArrayList<>())
            .setMembers(members)
            .setOwner(userDto)
            .build();

    final Set<ConstraintViolation<UserGroupDto>> constraintViolations =
        validator.validate(userGroupDto);

    assertEquals(7 <= length && length <= 25, constraintViolations.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(ints = {13, 14, 15, 16, 48, 49, 50, 51})
  void validate_when_differentAge_then_noConstraintViolationForCorrectAge(int age) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(5L)
            .setUser("Username")
            .setCity("Winti")
            .andOptionals()
            .setAge(age)
            .build();

    final ArrayList<UserDto> members = new ArrayList<>();
    members.add(userDto);

    final UserGroupDto userGroupDto =
        UserGroupDto.newBuilder()
            .andOptionals()
            .setLanguages(new ArrayList<>())
            .setMembers(members)
            .setOwner(userDto)
            .build();

    final Set<ConstraintViolation<UserGroupDto>> constraintViolations =
        validator.validate(userGroupDto);

    assertEquals(15 <= age && age <= 49, constraintViolations.isEmpty());
  }

  @Test
  void validate_when_validEmailAddress_then_noConstraintViolation() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(5L)
            .setUser("Username")
            .setCity("Winti")
            .andOptionals()
            .build();

    final Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDto);

    assertTrue(constraintViolations.isEmpty());
  }

  @Test
  void validate_when_invalidEmailAddress_then_constraintViolation() {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final UserDto userDto =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(5L)
            .setUser("Username")
            .setCity("Winti")
            .andOptionals()
            .build();

    final Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDto);

    assertTrue(constraintViolations.isEmpty());
  }
}
