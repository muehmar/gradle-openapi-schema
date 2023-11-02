package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import org.junit.jupiter.api.Test;

class JavaAdditionalPropertiesTest {

  @Test
  void getMapContainerType_when_called_then_correctMapContainerTypeCreated() {
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.integerType());

    final JavaType mapContainerType = javaAdditionalProperties.getMapContainerType();

    final JavaMapType expectedMapContainerType =
        JavaMapType.wrap(
            MapType.ofKeyAndValueType(
                StringType.noFormat(), javaAdditionalProperties.getType().getType()),
            TypeMappings.empty());

    assertEquals(expectedMapContainerType, mapContainerType);
  }
}
