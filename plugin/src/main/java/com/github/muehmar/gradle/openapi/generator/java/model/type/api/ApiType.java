package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import lombok.Value;

@Value
public class ApiType {
  QualifiedClassName className;
  ParameterizedClassName parameterizedClassName;
  ToApiTypeConversion toApiTypeConversion;
  FromApiTypeConversion fromApiTypeConversion;

  public static ApiType fromConversion(
      QualifiedClassName className, TypeConversion conversion, PList<JavaType> generics) {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromGenericClass(className, generics);
    final ToApiTypeConversion toApiTypeConversion =
        new ToApiTypeConversion(ConversionMethod.ofString(conversion.getToCustomType()));
    final FromApiTypeConversion fromApiTypeConversion =
        new FromApiTypeConversion(ConversionMethod.ofString(conversion.getFromCustomType()));
    return new ApiType(
        className, parameterizedClassName, toApiTypeConversion, fromApiTypeConversion);
  }
}
