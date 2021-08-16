package com.github.muehmar.gradle.openapi;

import OpenApiSchema.example.api.model.UserDto;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TestBuilder {

  @Test
  void build_when_optionalPropertySetTwiceAndEmptyTheSecondTime_then_propertyNotPresent() {
    final UserDto.Builder builder =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123L)
            .setUser("user")
            .setCity("city")
            .andOptionals();

    final UserDto userDto = builder.setAge(13).setAge(Optional.empty()).build();

    assertEquals(Optional.empty(), userDto.getAgeOptional());
  }

  @Test
  void builder_requiredMethodsDoNotHaveAPublicSetter() {
    final UserDto.Builder builder =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123L)
            .setUser("user")
            .setCity("city")
            .andOptionals();

    final Method[] declaredMethods = builder.getClass().getDeclaredMethods();

    for (Method declaredMethod : declaredMethods) {
      if (Modifier.isPublic(declaredMethod.getModifiers())) {
        assertFalse(declaredMethod.getName().equals("setId"));
        assertFalse(declaredMethod.getName().equals("setExternalId"));
        assertFalse(declaredMethod.getName().equals("setUser"));
        assertFalse(declaredMethod.getName().equals("setCity"));
      }
    }
  }
}
