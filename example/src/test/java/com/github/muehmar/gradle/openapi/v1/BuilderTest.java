package com.github.muehmar.gradle.openapi.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import openapischema.example.api.v1.model.SampleDto;
import org.junit.jupiter.api.Test;

class BuilderTest {

  @Test
  void build_when_optionalPropertySetTwiceAndEmptyTheSecondTime_then_propertyNotPresent() {
    final SampleDto dto =
        SampleDto.newBuilder()
            .setProp1("prop1")
            .setProp2(5)
            .andOptionals()
            .setProp3("prop3")
            .setProp3(Optional.empty())
            .build();

    assertEquals(Optional.empty(), dto.getProp3Opt());
  }

  @Test
  void builder_requiredMethodsDoNotHaveAPublicSetter() {
    final SampleDto.Builder builder =
        SampleDto.newBuilder().setProp1("prop1").setProp2(5).andOptionals();

    final Method[] declaredMethods = builder.getClass().getDeclaredMethods();

    for (Method declaredMethod : declaredMethods) {
      if (Modifier.isPublic(declaredMethod.getModifiers())) {
        assertNotEquals("setProp1", declaredMethod.getName());
        assertNotEquals("setProp2", declaredMethod.getName());
      }
    }
  }
}
