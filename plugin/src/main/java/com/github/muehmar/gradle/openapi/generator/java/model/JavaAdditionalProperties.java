package com.github.muehmar.gradle.openapi.generator.java.model;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.Value;

@Value
public class JavaAdditionalProperties {
  private static final Name MAP_PROPERTY_NAME = Name.ofString("additionalProperties");
  boolean allowed;
  JavaType type;

  public static JavaAdditionalProperties wrap(
      AdditionalProperties additionalProperties, TypeMappings typeMappings) {
    return new JavaAdditionalProperties(
        additionalProperties.isAllowed(),
        JavaType.wrap(additionalProperties.getType(), typeMappings));
  }

  public static JavaAdditionalProperties anyTypeAllowed() {
    return new JavaAdditionalProperties(true, JavaAnyType.create());
  }

  public Name getPropertyName() {
    return MAP_PROPERTY_NAME;
  }
}
