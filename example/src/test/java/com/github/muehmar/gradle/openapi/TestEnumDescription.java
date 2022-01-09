package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.model.UserDto;
import org.junit.jupiter.api.Test;

class TestEnumDescription {
  @Test
  void getDescription_when_called_then_correspondingDescriptionReturned() {
    assertEquals("User role", UserDto.RoleEnum.USER.getDescription());
    assertEquals("Administrator role", UserDto.RoleEnum.ADMIN.getDescription());
    assertEquals("Visitor role", UserDto.RoleEnum.VISITOR.getDescription());
  }
}
