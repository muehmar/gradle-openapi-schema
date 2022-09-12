package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import OpenApiSchema.example.api.v1.model.UserDto;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

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

    assertEquals(Optional.empty(), userDto.getAgeOpt());
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
        assertNotEquals("setId", declaredMethod.getName());
        assertNotEquals("setExternalId", declaredMethod.getName());
        assertNotEquals("setUser", declaredMethod.getName());
        assertNotEquals("setCity", declaredMethod.getName());
      }
    }
  }
}
